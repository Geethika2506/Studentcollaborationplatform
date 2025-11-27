import React, { useEffect, useState, useRef, useCallback } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getProjectById, getMessages, sendMessage, kickMember, joinProject } from "../services/Api";
import "../styles/Chat.css";

function Chat() {
    const { projectId } = useParams();
    const [project, setProject] = useState(null);
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState("");
    const [loading, setLoading] = useState(true);
    const [sending, setSending] = useState(false);
    const messagesEndRef = useRef(null);
    const navigate = useNavigate();
    const currentUserId = parseInt(localStorage.getItem("userId"));

    const fetchProjectDetails = useCallback(async () => {
        try {
            const response = await getProjectById(projectId);
            setProject(response.data);
            
            // Check if user is a member
            const isMember = response.data.members?.some(m => m.id === currentUserId);
            console.log("Am I a member?", isMember);
            console.log("My ID:", currentUserId);
            console.log("Members:", response.data.members);
            
            if (!isMember) {
                console.log("Not a member, auto-joining...");
                try {
                    await joinProject(projectId);
                    console.log("Successfully joined project");
                    // Refresh project details after joining
                    const updatedResponse = await getProjectById(projectId);
                    setProject(updatedResponse.data);
                    alert("You have been added to the project!");
                } catch (joinError) {
                    console.error("Error joining project:", joinError);
                    alert("You need to join this project first. Redirecting to dashboard...");
                    navigate("/dashboard");
                }
            }
        } catch (err) {
            console.error("Error fetching project:", err);
            if (err.response?.status === 403) {
                alert("You don't have permission to view this project");
            } else {
                alert("Failed to load project details");
            }
            navigate("/dashboard");
        }
    }, [projectId, navigate, currentUserId]);

    const fetchMessages = useCallback(async () => {
        try {
            const response = await getMessages(projectId);
            setMessages(response.data);
            setLoading(false);
        } catch (err) {
            console.error("Error fetching messages:", err);
            if (err.response?.status === 403) {
                console.log("403 error - user not authorized to view messages");
            }
            setLoading(false);
        }
    }, [projectId]);

    useEffect(() => {
        fetchProjectDetails();
        fetchMessages();
        const interval = setInterval(fetchMessages, 3000); // Poll every 3 seconds
        return () => clearInterval(interval);
    }, [fetchProjectDetails, fetchMessages]);

    useEffect(() => {
        scrollToBottom();
    }, [messages]);

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    };

    const handleSendMessage = async (e) => {
        e.preventDefault();
        
        // Trim and check if message is empty
        const trimmedMessage = newMessage.trim();
        if (!trimmedMessage) {
            console.log("Message is empty, not sending");
            return;
        }

        console.log("Attempting to send message:", trimmedMessage);
        setSending(true);

        try {
            // Send the message
            const response = await sendMessage(projectId, trimmedMessage);
            console.log("Message sent successfully:", response);
            
            // Clear input immediately
            setNewMessage("");
            
            // Fetch messages to update the chat
            await fetchMessages();
        } catch (err) {
            console.error("Error sending message:", err);
            console.error("Error response:", err.response?.data);
            
            // Show more detailed error
            const errorMessage = err.response?.data?.message || 
                               err.response?.data?.error || 
                               "Failed to send message. Please try again.";
            alert(errorMessage);
        } finally {
            setSending(false);
        }
    };

    const handleKickMember = async (memberId) => {
        if (!window.confirm("Are you sure you want to remove this member?")) return;

        try {
            await kickMember(projectId, memberId);
            alert("Member removed successfully");
            fetchProjectDetails();
            fetchMessages();
        } catch (err) {
            console.error("Error kicking member:", err);
            alert(err.response?.data?.error || "Failed to remove member");
        }
    };

    // Get current user's role
    const getCurrentUserRole = () => {
        if (!project) return null;
        const currentMember = project.members?.find(m => m.id === currentUserId);
        return currentMember?.role;
    };

    const currentUserRole = getCurrentUserRole();
    const canKickMembers = currentUserRole === 'ADMIN' || currentUserRole === 'OBSERVER';

    // Helper to get role badge
    const getRoleBadge = (role) => {
        if (role === 'ADMIN') return <span className="role-badge admin">Admin</span>;
        if (role === 'OBSERVER') return <span className="role-badge observer">Observer</span>;
        return null;
    };

    if (loading) {
        return <div className="loading">Loading chat...</div>;
    }

    if (!project) {
        return <div className="error">Project not found</div>;
    }

    return (
        <div className="chat-container">
            <div className="chat-sidebar">
                <div className="sidebar-header">
                    <h3>{project.title}</h3>
                    <button 
                        className="btn-back"
                        onClick={() => navigate("/dashboard")}
                    >
                        ← Back
                    </button>
                </div>

                <div className="project-details">
                    <p><strong>Description:</strong> {project.description}</p>
                    <p><strong>Creator:</strong> {project.creatorName}</p>
                    <p><strong>Observer:</strong> {project.observerName || "Not appointed"}</p>
                    <p><strong>Status:</strong> <span className={`status-${project.status.toLowerCase()}`}>{project.status}</span></p>
                    {currentUserRole && (
                        <p><strong>Your Role:</strong> {getRoleBadge(currentUserRole)}</p>
                    )}
                </div>

                <div className="members-section">
                    <h4>Members ({project.members?.length || 0})</h4>
                    <ul className="members-list">
                        {project.members?.map((member) => (
                            <li key={member.id} className="member-item">
                                <div className="member-info">
                                    <div className="member-name-row">
                                        <strong>{member.fullName}</strong>
                                        {getRoleBadge(member.role)}
                                    </div>
                                    <small>@{member.username}</small>
                                    {member.lastActivity && (
                                        <small className="last-active">
                                            Last active: {new Date(member.lastActivity).toLocaleString()}
                                        </small>
                                    )}
                                </div>
                                {canKickMembers && 
                                 member.role !== 'ADMIN' &&
                                 member.id !== currentUserId &&
                                 // Observers can't kick other observers
                                 !(currentUserRole === 'OBSERVER' && member.role === 'OBSERVER') && (
                                    <button
                                        className="btn-kick"
                                        onClick={() => handleKickMember(member.id)}
                                        title="Remove member"
                                    >
                                        ✕
                                    </button>
                                )}
                            </li>
                        ))}
                    </ul>
                </div>
            </div>

            <div className="chat-main">
                <div className="chat-messages">
                    {messages.length === 0 ? (
                        <div className="no-messages">
                            No messages yet. Start the conversation!
                        </div>
                    ) : (
                        messages.map((msg) => (
                            <div
                                key={msg.id}
                                className={`message ${msg.type.toLowerCase()} ${
                                    msg.senderId === currentUserId ? "own-message" : ""
                                }`}
                            >
                                {msg.type === "CHAT" && (
                                    <>
                                        <div className="message-header">
                                            <strong>{msg.senderName}</strong>
                                            <small>{new Date(msg.sentAt).toLocaleTimeString()}</small>
                                        </div>
                                        <div className="message-content">{msg.content}</div>
                                    </>
                                )}
                                {msg.type !== "CHAT" && (
                                    <div className="system-message">
                                        {msg.content}
                                    </div>
                                )}
                            </div>
                        ))
                    )}
                    <div ref={messagesEndRef} />
                </div>

                <form className="chat-input-form" onSubmit={handleSendMessage}>
                    <input
                        type="text"
                        value={newMessage}
                        onChange={(e) => setNewMessage(e.target.value)}
                        placeholder="Type your message..."
                        className="chat-input"
                        disabled={sending}
                    />
                    <button 
                        type="submit" 
                        className="btn-send"
                        disabled={sending || !newMessage.trim()}
                    >
                        {sending ? "Sending..." : "Send"}
                    </button>
                </form>
            </div>
        </div>
    );
}

export default Chat;
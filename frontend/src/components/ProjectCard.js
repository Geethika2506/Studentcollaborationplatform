import React from "react";
import styles from "../styles/ProjectCard.module.css";

const ProjectCard = ({ project, onJoin }) => (
  <div className={styles.card}>
    <div className={styles["card-title"]}>{project.title}</div>
    <div className={styles["card-desc"]}>{project.description}</div>
    <button className={styles["join-btn"]} onClick={() => onJoin(project.id)}>Join</button>
  </div>
);

export default ProjectCard;

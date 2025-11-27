import React from "react";
import styles from "../styles/ObserverPanel.module.css";

const ObserverPanel = ({ members, inactiveMembers }) => (
  <div className={styles.panel}>
    <h3>Observer Panel</h3>
    <ul className={styles["user-list"]}>
      {members.map((m) => (
        <li key={m.id}>
          {m.name} {inactiveMembers.includes(m.id) && <span style={{color: 'red'}}>(Inactive)</span>}
        </li>
      ))}
    </ul>
  </div>
);

export default ObserverPanel;

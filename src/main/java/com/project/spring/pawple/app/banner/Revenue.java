package com.project.spring.pawple.app.banner;

import jakarta.persistence.*;

@Entity
public class Revenue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int amount;

    // 기본 생성자 (
    public Revenue() {
    }
    // 생성자
    public Revenue(int amount) {
        this.amount = amount;
    }

    // Getter Setter
    public int getAmount() {
        return amount;
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }
    
    public Long getId() {
        return id;
    }
}

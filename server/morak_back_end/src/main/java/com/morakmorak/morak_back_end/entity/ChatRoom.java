package com.morakmorak.morak_back_end.entity;

import lombok.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnJava;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;

    private String roomName;

    @Builder.Default
    @OneToMany(mappedBy = "chatRoom")
    private List<ChatIn> chatIns = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "chatRoom")
    private List<Chat> chats = new ArrayList<>();

}

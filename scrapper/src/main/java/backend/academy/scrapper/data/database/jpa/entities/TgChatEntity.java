package backend.academy.scrapper.data.database.jpa.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tg_chats")
public class TgChatEntity {
    @Id
    @Column(name = "chat_id")
    private Long chatId;

    public TgChatEntity(Long chatId) {
        this.chatId = chatId;
    }

    @ManyToMany
    @JsonIgnore
    @JoinTable(
            name = "tag_on_chat",
            schema = "chats",
            joinColumns = @JoinColumn(name = "chat_id", referencedColumnName = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"chat_id", "tag_id"}))
    private Set<TagEntity> tags = new HashSet<>();

    @ManyToMany
    @JsonIgnore
    @JoinTable(
            name = "filter_on_chat",
            schema = "chats",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "filter_id"))
    private Set<FilterEntity> filters;
}

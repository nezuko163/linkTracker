package backend.academy.scrapper.data.repository.tracking.tracking;

public interface TrackingRepository {
    // возвращает id отношения ссылки и чата
    Long saveLinkOnChat(Long linkId, Long tgChatId);

    void saveTagOnRelation(Long relationId, Long tagId);

    void saveFilterOnRelation(Long relationId, Long filterId);

    void removeRelation(Long linkId, Long tgChatId);

    void removeRelation(String url, Long tgChatId);

    void removeAllRelationsFromChat(Long tgChatId);
}

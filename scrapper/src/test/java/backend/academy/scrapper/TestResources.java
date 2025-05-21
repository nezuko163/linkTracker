package backend.academy.scrapper;

import backend.academy.scrapper.domain.model.serviceLink.StackOverflowLink;

public class TestResources {
    public static final String URL_SO1 =
            "https://stackoverflow.com/questions/78785105/stateflow-value-changes-but-subscribers-are-not-notified";
    public static final StackOverflowLink LINK_SO1 = new StackOverflowLink(URL_SO1, 78785105L);

    public static final String URL_SO2 =
            "https://stackoverflow.com/questions/28352732/best-way-to-add-local-dependency-to-maven-project";
    public static final StackOverflowLink LINK_SO2 = new StackOverflowLink(URL_SO2, 28352732L);

    public static final String URL_SO3 =
            "https://stackoverflow.com/questions/22355301/how-to-include-a-spring-project-into-another-spring-project";
    public static final StackOverflowLink LINK_SO3 = new StackOverflowLink(URL_SO3, 22355301L);

    public static final Long CHAT_ID1 = 1020416851L;
    public static final Long CHAT_ID2 = 1020416852L;

    public static final String TAG1 = "work";
    public static final String TAG2 = "hobby";
    public static final String TAG3 = "dota";

    public static final String FILTER1 = "user:nezuko";
    public static final String FILTER2 = "lang:kotlin";
    public static final String FILTER3 = "country:ru";
}

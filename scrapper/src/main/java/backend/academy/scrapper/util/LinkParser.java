package backend.academy.scrapper.util;

import backend.academy.exceptions.ParsingError;
import backend.academy.scrapper.domain.model.CustomLink;
import backend.academy.scrapper.domain.model.ObservableService;
import backend.academy.scrapper.domain.model.serviceLink.GithubLink;
import backend.academy.scrapper.domain.model.serviceLink.StackOverflowLink;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class LinkParser {
    @SuppressWarnings("StringSplitter")
    public static CustomLink parseUrl(String url) {
        try {
            ObservableService service;
            CustomLink link;
            URI uri = new URI(url).normalize();
            String host = uri.getHost();
            if (host == null) throw new ParsingError("Неправильный хост", url);
            host = host.startsWith("www.") ? host.substring(4) : host;

            switch (host) {
                case "stackoverflow.com" -> service = ObservableService.STACK_OVERFLOW;
                case "github.com" -> service = ObservableService.GITHUB;
                default -> service = null;
            }

            switch (service) {
                case STACK_OVERFLOW -> {
                    String regex = "/questions/(\\d+)";

                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(uri.getPath());

                    if (!matcher.find()) throw new ParsingError("Неправильный формат Stackoverflow", url);

                    var number = matcher.group(1);

                    if (!StringUtils.isNumeric(number))
                        throw new ParsingError("Неправильный формат Stackoverflow", url);

                    link = new StackOverflowLink(url, Long.valueOf(number));
                }
                case GITHUB -> {
                    String regex = "^/([^/]+)/([^/]+)";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(uri.getPath());

                    if (!matcher.find()) throw new ParsingError("Неправильный формат Github", url);

                    var owner = matcher.group(1);
                    var repo = matcher.group(2);

                    link = new GithubLink(url, owner, repo);
                }

                case null -> throw new ParsingError("Нет возможнсои отслеживать сервис", url);
                default -> throw new ParsingError("Нет возможнсои отслеживать сервис", url);
            }
            return link;
        } catch (Exception e) {
            throw new ParsingError("Ошибка при парсинге ссылки", url);
        }
    }
}

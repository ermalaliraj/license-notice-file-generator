package eu.europa.ec.comp.elen.notice.remote;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.comp.elen.notice.common.NpmJsDependencyNameTxtConverter;
import eu.europa.ec.comp.elen.notice.common.TxtLinesReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class NoticeService {
    // How to use the data : https://docs.clearlydefined.io/using-data
    // REST API: https://api.clearlydefined.io/api-docs/
    private static final String POST_URL = "https://api.clearlydefined.io/notices";

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Enable to load data from previously saved JSON responses. The full response is predefined and not filtered by any
     * of specified full names.
     * File to be loaded depends on specified NoticeProvider.
     */
    private boolean useExistingResponses = true;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<RemoteNotice> retrieveNotices(Collection<String> fullNames, NoticeProvider noticeProvider)
            throws IOException, InterruptedException {
//        if (useExistingResponses) {
//            return retriveNoticesFromExistingData(noticeProvider);
//        }

        return retrieveNoticesFromRemoteService(fullNames, noticeProvider);
    }

    /**
     * Enable/ disable loading of existing disk data, per specified {@link NoticeProvider}.
     *
     * @param useExistingResponses <code>true</code> to load data from disk, <code>false</code> to query HTTP service.
     */
    public void setUseExistingResponses(boolean useExistingResponses) {
        this.useExistingResponses = useExistingResponses;
    }

    public List<RemoteNotice> retrieveFromJson(Reader reader, NoticeProvider noticeProvider)
            throws IOException {
        final String json = readJson(reader);
        final NoticesResponse noticesResponse = deserializeResponse(json);
        return toListOfNotices(noticesResponse, noticeProvider);
    }

    private List<RemoteNotice> retrieveNoticesFromRemoteService(Collection<String> fullNames, NoticeProvider noticeProvider)
            throws IOException, InterruptedException {
        var noticesRequest = buildRequest(fullNames, noticeProvider);
        var requestBody = serializeRequest(noticesRequest);
        final String body = executeRequest(requestBody);
        final NoticesResponse noticesResponse = deserializeResponse(body);
        return toListOfNotices(noticesResponse, noticeProvider);
    }

    private List<RemoteNotice> retriveNoticesFromExistingData(NoticeProvider noticeProvider) throws IOException {
        log.info("Using existing disk data, i.e not querying via {} ...", POST_URL);
        switch (noticeProvider) {
            case MAVENCENTRAL:
                return retrieveFromJson(getReader("/remote/maven_sample_response_2.json"), NoticeProvider.MAVENCENTRAL);
            case NPMJS:
                return retrieveFromJson(getReader("/remote/npmjs_sample_response.json"), NoticeProvider.NPMJS);
            default:
                throw new IllegalArgumentException("noticeProvider not found");
        }
    }

    private static String readJson(Reader reader) throws IOException {
        return TxtLinesReader.readAllLines(reader)
                .stream()
                .collect(Collectors.joining("\r\n"));
    }

    private String executeRequest(String requestBody) throws IOException, InterruptedException {
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .uri(URI.create(POST_URL))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        log.info(String.format("Executing POST to URL '%s' using body:\n%s", POST_URL, requestBody));

        final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException(String.format("Got status %d and body: '%s'", response.statusCode(), response.body()));
        }
        return response.body();

//      final String pathOnClasspath = "/remote/maven_sample_response_2.json";
//        final String pathOnClasspath = "/remote/npmjs_sample_response.json";
//        return loadPredefinedResponse(pathOnClasspath);
    }

    private Reader getReader(String pathOnClasspath) throws IOException {
        log.info("Loading JSON content from classpath resource: '{}' ...", pathOnClasspath);
        try {
            final URL url = this.getClass().getResource(pathOnClasspath);
            final Path path = Path.of(url.toURI());
            return Files.newBufferedReader(path);
        } catch (URISyntaxException e) {
            throw new IOException(String.format("Could not read content from path: %s", pathOnClasspath), e);
        }
    }

    private NoticesResponse deserializeResponse(String body) throws JsonProcessingException {
        return objectMapper.readValue(body, NoticesResponse.class);
    }

    private List<RemoteNotice> toListOfNotices(NoticesResponse noticesResponse, NoticeProvider noticeProvider) {
        var warnings = noticesResponse.getSummary().getWarnings();
        logWarnings(warnings, noticeProvider);

        final List<RemoteNotice> remoteNotices = noticesResponse
                .getContent()
                .getPackages()
                .stream()
                .map(aPackage -> toNotice(aPackage, noticeProvider))
                .sorted(Comparator.comparing(RemoteNotice::getFullName))
                .collect(Collectors.toList());
        log.warn("Total remoteNotices: {}", remoteNotices.size());
        logMissingLicenseText(remoteNotices);

        return remoteNotices;
    }

    private void logMissingLicenseText(List<RemoteNotice> remoteNotices) {
        final List<String> noticesWithoutValidLicenseText = remoteNotices
                .stream()
                .filter(notice -> !notice.hasValidText())
                .map(notice -> notice.getFullName() + " for license " + notice.getLicense().orElse("<none>"))
                .collect(Collectors.toList());
        final String namesWithoutValidLicenseText = noticesWithoutValidLicenseText
                .stream()
                .collect(Collectors.joining(",\r\n\t"));
        if (!namesWithoutValidLicenseText.isBlank()) {
            log.warn("Could not find valid license text via '{}' for following {} names:\r\n\t{}",
                    POST_URL, noticesWithoutValidLicenseText.size(), namesWithoutValidLicenseText);
        }
    }

    private void logWarnings(NoticesResponse.Warnings warnings, NoticeProvider noticeProvider) {
        String namesWithNoDefinition = warnings
                .getNoDefinition()
                .stream()
                .map(nameWithProvider -> extractFullNameWithVersion(nameWithProvider, noticeProvider))
                .collect(Collectors.joining(",\r\n\t"));
        if (!namesWithNoDefinition.isBlank()) {
            log.warn("Could not find any definition via '{}' for following {} names:\r\n\t{}",
                    POST_URL, warnings.getNoDefinition().size(), namesWithNoDefinition);
        }

        String namesWithNoCopyrights = warnings
                .getNoCopyright()
                .stream()
                .map(nameWithProvider -> extractFullNameWithVersion(nameWithProvider, noticeProvider))
                .collect(Collectors.joining(",\r\n\t"));
        if (!namesWithNoCopyrights.isBlank()) {
            log.warn("Could not find any copyrights via '{}' for following {} names:\r\n\t{}",
                    POST_URL, warnings.getNoCopyright().size(), namesWithNoCopyrights);
        }

        String namesWithNoLicense = warnings
                .getNoLicense()
                .stream()
                .map(nameWithProvider -> extractFullNameWithVersion(nameWithProvider, noticeProvider))
                .collect(Collectors.joining(",\r\n\t"));
        if (!namesWithNoLicense.isBlank()) {
            log.warn("Could not find any license via '{}' for following {} names:\r\n\t{}",
                    POST_URL, warnings.getNoLicense().size(), namesWithNoLicense);
        }
    }

    private RemoteNotice toNotice(NoticesResponse.Package aPackage, NoticeProvider noticeProvider) {
        var notice = new RemoteNotice();
        notice.setDefined(true);
        var fullName = convertFullName(aPackage.getName(), aPackage.getVersion(), noticeProvider);
        notice.setFullName(fullName);
        notice.setName(convertName(aPackage.getName(), noticeProvider));
        if (null != aPackage.getLicense() && !aPackage.getLicense().isBlank()) {
            notice.setLicense(aPackage.getLicense());
        }
        if (null != aPackage.getCopyrights() && !aPackage.getCopyrights().isEmpty()) {
            notice.setCopyrights(aPackage.getCopyrights());
        }
        notice.setVersion(aPackage.getVersion());
        notice.setWebsite(aPackage.getWebsite());
        notice.setText(aPackage.getText());
        return notice;
    }

    private String convertFullName(String name, String version, NoticeProvider noticeProvider) {
        switch (noticeProvider) {
            // fullName is like: org.springframework.boot/spring-boot-starter-test
            case MAVENCENTRAL:
                return name.replace("/", ":") + ":" + version;
            // fullName is like: @angular/elements
            // return name as it is
            case NPMJS:
                return name + "@" + version;
            default:
                throw new IllegalArgumentException("noticeProvider not found");
        }
    }

    private String convertName(String name, NoticeProvider noticeProvider) {
        switch (noticeProvider) {
            // fullName is like: org.springframework.boot/spring-boot-starter-test
            case MAVENCENTRAL:
                return name.replace("/", ":");
            // fullName is like: @angular/elements
            // return name as it is
            case NPMJS:
                return name;
            default:
                throw new IllegalArgumentException("noticeProvider not found");
        }
    }

    private String extractFullNameWithVersion(String nameWithProvider, NoticeProvider noticeProvider) {
        switch (noticeProvider) {
            case MAVENCENTRAL:
                // value is like: maven/mavencentral/org.springframework.boot/spring-boot-starter-test/2.7.3
                return nameWithProvider.substring("maven/mavencentral/".length()).replace("/", ":");
            case NPMJS:
                // value is like: npm/npmjs/@angular/elements/7.2.15
                return nameWithProvider.substring("npm/npmjs/".length());
            default:
                throw new IllegalArgumentException("Unknown NoticeProvider " + noticeProvider);
        }
    }

    private String serializeRequest(NoticesRequest noticesRequest) throws JsonProcessingException {
        return objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(noticesRequest);
    }

    private NoticesRequest buildRequest(Collection<String> fullNames, NoticeProvider noticeProvider) {
        List<String> coordinates = null;
        switch (noticeProvider) {
            case MAVENCENTRAL:
                coordinates =  toMavenCentralFormat(fullNames);
                break;
            case NPMJS:
                coordinates =  toNpmJsFormat(fullNames);
                break;
        }

        final NoticesRequest request = new NoticesRequest();
        request.setCoordinates(coordinates);
        request.setRenderer("json");
        return request;
    }

    private static List<String> toNpmJsFormat(Collection<String> fullNames) {
        // a full name is expected to be like @ngx-translate/http-loader@4.0.0
        // generate names like: npm/npmjs/@ngx-translate/http-loader/4.0.0
        // or like: d3-color@1.4.1
        // and generate names like npm/npmjs/-/d3-color/1.4.1
        var converter = new NpmJsDependencyNameTxtConverter();
        return fullNames
                .stream()
                .map(converter::convert)
                .map(depName -> "npm/npmjs/" + depName.getNamespace() + "/" + depName.getName() + "/" + depName.getVersion())
                .collect(Collectors.toList());
    }

    private static List<String> toMavenCentralFormat(Collection<String> fullNames) {
        // a full name is expected to be like: org.springframework.boot:spring-boot-starter-test:2.7.3
        // generate names like: maven/mavencentral/org.springframework.boot/spring-boot-starter-test/2.7.3
        return fullNames
                .stream()
                .map(fullName -> "maven/mavencentral/" + fullName.replace(":", "/"))
                .collect(Collectors.toList());
    }

    public enum NoticeProvider {
        MAVENCENTRAL,
        NPMJS
    }
}

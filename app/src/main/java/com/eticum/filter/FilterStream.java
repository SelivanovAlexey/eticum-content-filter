package com.eticum.filter;

import com.eticum.api.http.model.Mode;
import com.eticum.api.http.model.Profile;


import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilterStream {

    private final static String URL_REGEX = "^(.*:)//([A-Za-z0-9\\-.]+)(:[0-9]+)?(.*)$";

    public final static int REASON_OK = 0;
    public final static int REASON_AGE = 1;
    public final static int REASON_NOT_ALLOW = 2;
    public final static int REASON_DISALLOW = 3;
    public final static int REASON_UNDEFINED = -1;

    private final Profile profile;
    private Integer reason = REASON_UNDEFINED;
    private final Mode mode;

    private FilterStream(Profile profile) {
        this.profile = profile;
        this.mode = profile.getMode();
    }

    public static FilterStream of(Profile profile) {
        return new FilterStream(profile);
    }

    public FilterStream checkAge(int age) {
        if (!reason.equals(REASON_UNDEFINED)) return this;
        reason = age > profile.getAge() ? REASON_AGE : REASON_UNDEFINED;
        return this;
    }

    public FilterStream checkCategories(List<Integer> categories) {
        if (!reason.equals(REASON_UNDEFINED)) return this;
        switch (mode) {
            case allow:
                reason = profile.getCategories().containsAll(categories) ? REASON_OK : REASON_NOT_ALLOW;
                break;
            case deny:
                boolean res = profile.getCategories().stream().anyMatch(categories::contains);
                reason = res ? REASON_DISALLOW : REASON_OK;
                break;
            default:
                reason = REASON_OK;
                break;
        }
        return this;
    }

    public int getAccess() {
        if (mode.equals(Mode.info)) return REASON_OK;
        else return reason;
    }

    public FilterStream checkUrlAccess(String uri) {
        Matcher targetUriMatcher = Pattern.compile(URL_REGEX).matcher(uri);
        if (targetUriMatcher.find()) {
            profile.getAllowUrls().forEach(listUri -> {
                Matcher listUriMatcher = Pattern.compile(URL_REGEX).matcher(listUri);
                if (listUriMatcher.find() &&
                        (Objects.equals(listUriMatcher.group(2), targetUriMatcher.group(2)) ||
                                ("www." + listUriMatcher.group(2)).equals(targetUriMatcher.group(2)))) {
                    reason = REASON_OK;
                }
            });
            profile.getDenyUrls().forEach(listUri -> {
                Matcher listUriMatcher = Pattern.compile(URL_REGEX).matcher(listUri);
                if (listUriMatcher.find() &&
                        (Objects.equals(listUriMatcher.group(2), targetUriMatcher.group(2)) ||
                                ("www." + listUriMatcher.group(2)).equals(targetUriMatcher.group(2)))) {
                    reason = REASON_DISALLOW;
                }
            });
        }
        return this;
    }
}

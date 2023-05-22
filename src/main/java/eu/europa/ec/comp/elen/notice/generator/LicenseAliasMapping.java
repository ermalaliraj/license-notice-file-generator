package eu.europa.ec.comp.elen.notice.generator;

import org.silentsoft.oss.License;
import org.silentsoft.oss.LicenseDictionary;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Additional names for licenses. The 'TO' alias is supposed to be known by the system, i.e. to have associated a
 * license text.
 */
public class LicenseAliasMapping {
    private static Map<String, String> ALIAS_TO_ALIAS = new HashMap<>();

    static {
        addMapping("Eclipse Public License, Version 1.0", "Eclipse Public License v1.0");
        addMapping("BSD License 3", "BSD-3-Clause");
        addMappings(new String[]{"ASF 2.0", "Apache 2", "The Apache License, Version 2.0"},
                "Apache License 2.0");
        addMapping("GNU Lesser General Public License (LGPL), version 2.1 or later",
                "GNU Lesser General Public License v2.1 or later");
        addMappings(new String[]{
                "GENERAL PUBLIC LICENSE, version 3 (GPL-3.0)",
                "GNU LESSER GENERAL PUBLIC LICENSE, version 3 (LGPL-3.0)",
                "GNU General Lesser Public License (LGPL) version 3.0",
                "GNU Lesser General Public License 3.0"
        }, "GNU General Public License v3.0");
    }

    public static boolean addMapping(String fromAlias, String toAlias) {
        Objects.requireNonNull(fromAlias);
        Objects.requireNonNull(toAlias);
        var prevMapping = ALIAS_TO_ALIAS.putIfAbsent(fromAlias, toAlias);
        return null == prevMapping;
    }

    public static void addMappings(String[] fromAliases, String toAlias) {
        Objects.requireNonNull(fromAliases);
        Objects.requireNonNull(toAlias);
        Arrays.stream(fromAliases)
              .forEach(fromAlias -> addMapping(fromAlias, toAlias));
    }

    public static String mapIfNeeded(String alias) {
        return ALIAS_TO_ALIAS.getOrDefault(alias, alias);
    }

    public static Optional<License> getLicense(String alias) {
        var mappedAlias = mapIfNeeded(alias);
        return Optional.ofNullable(LicenseDictionary.get(mappedAlias));
    }

    public static boolean hasValidLicense(String alias) {
        return getLicense(alias).isPresent();
    }
}

Generate Notice files for Private and Trusted apps
---

## HowTo for Java components

1. Add and enable Maven plugin  `org.codehaus.mojo:license-maven-plugin` inside root pom.xml,
   and enable goal `aggregate-add-third-party`.

   Proposed configuration:
   ```xml
    <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>license-maven-plugin</artifactId>
        <version>2.0.0</version>
        <configuration>
            <skipAggregateAddThirdParty>false</skipAggregateAddThirdParty>
        </configuration>
        <executions>
            <execution>
                <id>download-licenses</id>
                <goals>
                    <goal>aggregate-add-third-party</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
    ``` 

2. Go in the folder of the REST projects and trigger execution of the plugin by calling ```mvn package```
   with any additional profile or options to skip tests.

3. Save the THIRD-PARTY.txt file in a location accessible to current project.

4. Edit content inside generate THIRD-PARTY.txt files to 
   * keep only one license per dependency and 
   * make sure there are no chars like round parentheses (like ')' or '(') inside the name of the dependency.
   
   This is because implemented parser for THIRD-PART.txt uses parantheses as separators to detect license, maven
   dependency name and website.

5. Adapt class `BackEndNoticeGenerator` to use the right TXT files, execute it and monitor output.
   Code might need to be better adapted for specific needs.

## HowTo for Npm JS components
1. Add and enable dev dependency `license-checker` inside each of the Angular projects. Tests were done with version `25.0.1`.

2. For each Angular project execute following command from the folder keeping the `package.json` file:
   ```npx license-checker --production --csv --out 3rd-party-licenses.csv```

3. Save the 3rd-party-licenses.csv file in a location accessible to current project.

4. Edit content inside generated CSV files and keep only one license per dependency. 

5. Adapt class `FrontEndNoticeGenerator` to use the right CSV files, execute it and monitor output.
   Code might need to be better adapted for specific needs.
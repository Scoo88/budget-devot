package hr.scuric.dewallet.common.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {
    @Value("${application.url}")
    private String applicationUrl;

    @Value("${application.documentation.name}")
    private String applicationName;

    @Value("${application.documentation.description}")
    private String applicationDescription;

    @Value("${application.documentation.version}")
    private String applicationVersion;

    @Value("${application.documentation.email}")
    private String applicationEmail;

    @Bean
    public OpenAPI openApiInformation() {
        Server localServer = new Server().url(applicationUrl);

        Contact contact = new Contact().email(applicationEmail);

        Info info = new Info().contact(contact).description(applicationDescription).title(applicationName).version(
                applicationVersion);

        return new OpenAPI().info(info).addServersItem(localServer);
    }

    // all
    @Bean
    public GroupedOpenApi groupAll() {
        return GroupedOpenApi.builder().group("All API").pathsToMatch("/api/v1/budget/**").build();
    }

    @Bean
    public GroupedOpenApi groupClient() {
        return GroupedOpenApi.builder()
                .group("Client API (group)")
                .pathsToMatch("/api/v1/budget/client*/**")
                .build();
    }

    @Bean
    public GroupedOpenApi groupCategory() {
        return GroupedOpenApi.builder()
                .group("Category API (group)")
                .pathsToMatch("/api/v1/budget/category/**")
                .build();
    }

    @Bean
    public GroupedOpenApi groupExpense() {
        return GroupedOpenApi.builder()
                .group("Expense API (group)")
                .pathsToMatch("/api/v1/budget/expense/**")
                .build();
    }
}

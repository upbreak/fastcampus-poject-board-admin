package com.fastcampus.projectboardadmin.service;

import com.fastcampus.projectboardadmin.domain.constant.RoleType;
import com.fastcampus.projectboardadmin.dto.ArticleDto;
import com.fastcampus.projectboardadmin.dto.UserAccountDto;
import com.fastcampus.projectboardadmin.dto.properties.ProjectProperties;
import com.fastcampus.projectboardadmin.dto.reponse.ArticleClientResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withTooManyRequests;

@ActiveProfiles("testdb")
@DisplayName("비즈니스 로직 - 게시글 관리")
class ArticleManagementServiceTest {

//    @Disabled("실제 API 호출 결과 관찰용이므로 평상시엔 비활성화")
    @DisplayName("실제 API 호출 테스트")
    @SpringBootTest
    @Nested
    class RealRestTest{
        private final ArticleManagementService sut;

        public RealRestTest(@Autowired ArticleManagementService sut) {
            this.sut = sut;
        }

        @DisplayName("게시글 API를 호출하면, 게시글을 가져온다.")
        @Test
        void givenNoting_whenCallingArticleApi_thenReturnArticleList() {
            // Given

            // When
            List<ArticleDto> result = sut.getArticles();

            // Then
            System.out.println(result.stream().findFirst());
            assertThat(result).isNotNull();
        }
    }

    @DisplayName("API mocking 테스트")
    @EnableConfigurationProperties(ProjectProperties.class)
    @AutoConfigureWebClient(registerRestTemplate = true)
    @RestClientTest(ArticleManagementService.class)
    @Nested
    class RestTemplateTest{

        private final ArticleManagementService sut;
        private final ProjectProperties projectProperties;
        private final MockRestServiceServer server;
        private final ObjectMapper mapper;

        @Autowired
        public RestTemplateTest(ArticleManagementService sut, ProjectProperties projectProperties, MockRestServiceServer server, ObjectMapper mapper) {
            this.sut = sut;
            this.projectProperties = projectProperties;
            this.server = server;
            this.mapper = mapper;
        }

        @DisplayName("게시글 목록 API를 호출하면, 게시글들을 가져온다.")
        @Test
        void givenNoting_whenCallingArticleApi_thenReturnArticleList() throws JsonProcessingException {
            // Given
            ArticleDto expectedArticle = createArticleDto("제목", "글");
            ArticleClientResponse expectedReponse = ArticleClientResponse.of(List.of(expectedArticle));
            server.expect(requestTo(projectProperties.board().url() + "/api/articles?size=10000"))
                    .andRespond(withSuccess(
                            mapper.writeValueAsString(expectedReponse)
                            , MediaType.APPLICATION_JSON
                    ));

            // When
            List<ArticleDto> results = sut.getArticles();

            // Then
            assertThat(results).first()
                    .hasFieldOrPropertyWithValue("id", expectedArticle.id())
                    .hasFieldOrPropertyWithValue("title", expectedArticle.title())
                    .hasFieldOrPropertyWithValue("content", expectedArticle.content())
                    .hasFieldOrPropertyWithValue("useAccount.nickname", expectedArticle.userAccountDto().nickname());
            server.verify();
        }

        @DisplayName("게시글 목록 API를 호출하면, 게시글을 가져온다.")
        @Test
        void givenNoting_whenCallingArticleApi_thenReturnArticle() throws JsonProcessingException {
            // Given
            Long articleId = 1L;
            ArticleDto expectedArticle = createArticleDto("제목", "글");
            server.expect(requestTo(projectProperties.board().url() + "/api/articles/" + articleId))
                    .andRespond(withSuccess(
                            mapper.writeValueAsString(expectedArticle)
                            , MediaType.APPLICATION_JSON
                    ));

            // When
            ArticleDto results = sut.getArticle(articleId);

            // Then
            assertThat(results)
                    .hasFieldOrPropertyWithValue("id", articleId)
                    .hasFieldOrPropertyWithValue("title", expectedArticle.title())
                    .hasFieldOrPropertyWithValue("content", expectedArticle.content())
                    .hasFieldOrPropertyWithValue("useAccount.nickname", expectedArticle.userAccountDto().nickname());
            server.verify();
        }

        @DisplayName("게시글 목록 API를 호출하면, 게시글을 삭제한다.")
        @Test
        void givenNoting_whenCallingDeleteArticleApi_thenDeleteArticle() {
            // Given
            Long articleId = 1L;
            server.expect(requestTo(projectProperties.board().url() + "/api/articles/" + articleId))
                    .andExpect(method(HttpMethod.DELETE))
                    .andRespond(withSuccess());

            // When
            sut.deleteArticle(articleId);

            // Then
            server.verify();
        }
    }
    private ArticleDto createArticleDto(String title, String content) {
        return ArticleDto.of(
                1L
                , createUserAccountDto()
                , title
                , content
                , null
                , LocalDateTime.now()
                , "Jinwoo"
                , LocalDateTime.now()
                , "Jinwoo"
        );
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                "jinwoo"
                , Set.of(RoleType.ADMIN)
                , "jinwoo@email.com"
                , "jinwoo"
                , "memo"
        );
    }
}
package com.project.bangjjack.domain.chat.application;

import com.project.bangjjack.domain.chat.application.dto.request.WebSocketInboundMessage;
import com.project.bangjjack.domain.chat.application.dto.request.WebSocketMessageType;
import com.project.bangjjack.domain.chat.application.exception.InvalidWsMessageFormatException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("WebSocketInboundMessage 검증 시")
class WebSocketInboundMessageValidateTest {

    @Nested
    @DisplayName("roomId가 null이면")
    class RoomIdNull {

        @Test
        @DisplayName("모든 타입에서 InvalidWsMessageFormatException이 발생한다")
        void 모든_타입에서_예외가_발생한다() {
            for (WebSocketMessageType type : WebSocketMessageType.values()) {
                WebSocketInboundMessage msg = new WebSocketInboundMessage(type, null, null, 1L);
                assertThatThrownBy(msg::validate)
                        .isInstanceOf(InvalidWsMessageFormatException.class);
            }
        }
    }

    @Nested
    @DisplayName("READ 타입에서")
    class ReadType {

        @Test
        @DisplayName("messageId가 null이면 InvalidWsMessageFormatException이 발생한다")
        void messageId가_null이면_예외가_발생한다() {
            WebSocketInboundMessage msg = new WebSocketInboundMessage(WebSocketMessageType.READ, 10L, null, null);
            assertThatThrownBy(msg::validate)
                    .isInstanceOf(InvalidWsMessageFormatException.class);
        }

        @ParameterizedTest
        @ValueSource(longs = {0L, -1L, -100L})
        @DisplayName("messageId가 0 이하이면 InvalidWsMessageFormatException이 발생한다")
        void messageId가_0이하이면_예외가_발생한다(long invalidId) {
            WebSocketInboundMessage msg = new WebSocketInboundMessage(WebSocketMessageType.READ, 10L, null, invalidId);
            assertThatThrownBy(msg::validate)
                    .isInstanceOf(InvalidWsMessageFormatException.class);
        }

        @Test
        @DisplayName("roomId와 양수 messageId가 있으면 예외가 발생하지 않는다")
        void 필수_필드가_모두_있으면_예외가_발생하지_않는다() {
            WebSocketInboundMessage msg = new WebSocketInboundMessage(WebSocketMessageType.READ, 10L, null, 100L);
            assertThatCode(msg::validate).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("READ 외 타입에서")
    class NonReadType {

        @Test
        @DisplayName("messageId가 없어도 예외가 발생하지 않는다")
        void messageId_없어도_예외가_발생하지_않는다() {
            for (WebSocketMessageType type : new WebSocketMessageType[]{
                    WebSocketMessageType.SUBSCRIBE, WebSocketMessageType.UNSUBSCRIBE, WebSocketMessageType.SEND
            }) {
                WebSocketInboundMessage msg = new WebSocketInboundMessage(type, 10L, "내용", null);
                assertThatCode(msg::validate).doesNotThrowAnyException();
            }
        }
    }
}

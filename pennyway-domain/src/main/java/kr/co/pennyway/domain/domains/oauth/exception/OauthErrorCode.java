package kr.co.pennyway.domain.domains.oauth.exception;

import kr.co.pennyway.common.exception.BaseErrorCode;
import kr.co.pennyway.common.exception.CausedBy;
import kr.co.pennyway.common.exception.ReasonCode;
import kr.co.pennyway.common.exception.StatusCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OauthErrorCode implements BaseErrorCode {
    /* 400 Bad Request */
    ALREADY_SIGNUP_OAUTH(StatusCode.BAD_REQUEST, ReasonCode.INVALID_REQUEST, "이미 해당 제공자로 가입된 사용자입니다."),
    INVALID_OAUTH_SYNC_REQUEST(StatusCode.BAD_REQUEST, ReasonCode.INVALID_REQUEST, "Oauth 동기화 요청이 잘못되었습니다."),

    /* 401 Unauthorized */
    NOT_MATCHED_OAUTH_ID(StatusCode.UNAUTHORIZED, ReasonCode.MISSING_OR_INVALID_AUTHENTICATION_CREDENTIALS, "OAuth ID가 일치하지 않습니다."),

    /* 422 Unprocessable Entity */
    INVALID_PROVIDER(StatusCode.UNPROCESSABLE_CONTENT, ReasonCode.TYPE_MISMATCH_ERROR_IN_REQUEST_BODY, "유효하지 않은 제공자입니다.");

    private final StatusCode statusCode;
    private final ReasonCode reasonCode;
    private final String message;

    @Override
    public CausedBy causedBy() {
        return CausedBy.of(statusCode, reasonCode);
    }

    @Override
    public String getExplainError() throws NoSuchFieldError {
        return message;
    }
}
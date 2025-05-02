package com.sejong.project.onair.global.exception.codes;

import com.sejong.project.onair.global.exception.codes.reason.Reason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    /**
     * ******************************* Global Error CodeList ***************************************
     * HTTP Status Code
     * 400 : Bad Request
     * 401 : Unauthorized
     * 403 : Forbidden
     * 404 : Not Found
     * 500 : Internal Server Error
     * *********************************************************************************************
     */
    // 잘못된 서버 요청
    BAD_REQUEST_ERROR(HttpStatus.BAD_REQUEST, "G001", "Bad Request Exception"),

    // @RequestBody 데이터 미 존재
    REQUEST_BODY_MISSING_ERROR(HttpStatus.BAD_REQUEST, "G002", "Required request body is missing"),

    // 유효하지 않은 타입
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "G003", " Invalid Type Value"),

    // Request Parameter 로 데이터가 전달되지 않을 경우
    MISSING_REQUEST_PARAMETER_ERROR(HttpStatus.BAD_REQUEST, "G004", "Missing Servlet RequestParameter Exception"),

    // 입력/출력 값이 유효하지 않음
    IO_ERROR(HttpStatus.BAD_REQUEST, "G005", "I/O Exception"),

    // com.google.gson JSON 파싱 실패
    JSON_PARSE_ERROR(HttpStatus.BAD_REQUEST, "G006", "JsonParseException"),

    // com.fasterxml.jackson.core Processing Error
    JACKSON_PROCESS_ERROR(HttpStatus.BAD_REQUEST, "G007", "com.fasterxml.jackson.core Exception"),

    // 권한이 없음
    FORBIDDEN_ERROR(HttpStatus.FORBIDDEN, "G008", "Forbidden Exception"),

    // 서버로 요청한 리소스가 존재하지 않음
    NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "G009", "Not Found Exception"),

    // NULL Point Exception 발생
    NULL_POINT_ERROR(HttpStatus.NOT_FOUND, "G010", "Null Point Exception"),

    // @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음
    NOT_VALID_ERROR(HttpStatus.NOT_FOUND, "G011", "handle Validation Exception"),

    // @RequestBody 및 @RequestParam, @PathVariable 값이 유효하지 않음
    NOT_VALID_HEADER_ERROR(HttpStatus.NOT_FOUND, "G012", "Header에 데이터가 존재하지 않는 경우 "),

    //인원 초과됨
    OVER_NUMBER_PEOPLE(HttpStatus.BAD_REQUEST,"G013","인원 수가 초과되어 입장 불가능"),

    //방 이미 입장함
    ALREADY_ENTER_ROOM(HttpStatus.BAD_REQUEST,"G014","현재 방에 이미 입장을 함"),
    
    //방을 찾을수 없음
    ROOM_NOT_FOUND(HttpStatus.BAD_REQUEST,"G015","방을 찾을 수 없음"),
    // 4xx : client error
    
    //일정없음
    SCHEDULE_NOT_FOUND(HttpStatus.BAD_REQUEST,"G016","일정을 찾을 수 없음"),

    //평가할거 없음
    EVALUATION_NOT_FOUND(HttpStatus.BAD_REQUEST,"G017","평가항목 찾을 수 없음"),

    SHORT_NUMBER_PEOPLE(HttpStatus.BAD_REQUEST,"G018","방 인원이 아직 모자름"),

    NOT_VALID_NUMBER_PEOPLE(HttpStatus.BAD_REQUEST,"G019","방 인원수 설정이 맞지 않음"),

    NOT_VALID_ROLE(HttpStatus.BAD_REQUEST,"G020","리더가 아니라 해당 기능은 불가"),

    NOT_VALID_MEMBERFOOD(HttpStatus.BAD_REQUEST,"G021","멤버 음식이 널 값이다."),

    POST_NOT_FOUND(HttpStatus.BAD_REQUEST,"POST-0000","게시글을 찾을 수 없음"),
    
    //자잘한 에러
    SEARCH_KEYWORD_TOO_SHORT(HttpStatus.BAD_REQUEST, "KEYWORD-0000", "검색어는 3글자부터 입력하세요."),

    //바인딩 에러
    BINDING_ERROR(HttpStatus.BAD_REQUEST, "BINDING-0000", "바인딩에 실패했습니다."),

    FILE_EXTENSION_ERROR(HttpStatus.BAD_REQUEST, "File-0000", "파일 확장자가 잘못되었습니다."),
    FILE_READ_ERROR(HttpStatus.BAD_REQUEST, "File-0001", "파일을 읽어오는데 실패하였습니다."),
    FILE_NOT_FOUND(HttpStatus.BAD_REQUEST, "File-0002", "해당 ID를 가진 파일을 찾을 수 없습니다."),
    FILELOG_NOT_FOUND(HttpStatus.BAD_REQUEST, "File-0003", "파일 업로드 기록을 가져올 수 없습니다."),

    //로그인 에러
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "LOGIN-0001", "이메일이 잘못됨"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON-0000", "잘못된 요청입니다."),
    EXIST_EMAIL(HttpStatus.BAD_REQUEST, "COMMON-0002", "이미 존재하는 회원입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER-0000", "존재하지 않는 회원입니다."),
    PASSWORD_ERROR(HttpStatus.BAD_REQUEST, "LOGIN-0000", "잘못된 비밀번호입니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "TOKEN-0001", "토큰ㅇ"),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN-0000", "토큰 오류"),
    EMPTY_TOKEN_PROVIDED(HttpStatus.UNAUTHORIZED, "TOKEN-0002", "토큰 텅텅"),
    INVALID_EMAIL_OR_PASSWORD(HttpStatus.NOT_FOUND, "MEMBER-0001", "유효하지 않는 이메일, 비번"),
    BAD_REQUEST_INGRANT(HttpStatus.BAD_REQUEST, "MEMBER-0002", "구글 권한이 제대로 넘어오지 않음."),

    // 5xx : server error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER-0000", "서버 에러");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public Reason.ReasonDto getReasonHttpStatus() {
        return Reason.ReasonDto.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}

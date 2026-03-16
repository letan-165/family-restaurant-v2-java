package family.main.project.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // ===== SERVICE (1001 – 1999) =====
    // === USER ===
    USER_NO_EXISTS(1001, "User not found", HttpStatus.BAD_REQUEST),
    USER_EXISTS(1002, "User existed", HttpStatus.BAD_REQUEST),
    USER_INVALID(1003, "User does not match token", HttpStatus.BAD_REQUEST),
    PROFILE_NO_EXISTS(1004, "User not found", HttpStatus.BAD_REQUEST),
    ITEM_NO_EXISTS(1005, "Item not found", HttpStatus.BAD_REQUEST),
    ORDER_NO_EXISTS(1006, "Order not found", HttpStatus.BAD_REQUEST),

    // ===== AUTH (2001 – 2999) =====
    UNAUTHORIZED(2001, "You don't have permission", HttpStatus.FORBIDDEN),
    UNAUTHENTICATED(2002, "Token not authenticated", HttpStatus.UNAUTHORIZED),
    PASSWORD_INVALID(2003, "Password invalid", HttpStatus.BAD_REQUEST),
    TOKEN_LOGOUT(2004, "Token had logout", HttpStatus.BAD_REQUEST),
    PARSE_TOKEN_FAIL(2005, "Jwt invalid", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_KEYCLOAK(2006, "You don't have permission access keycloak", HttpStatus.FORBIDDEN),

    // ===== ENUM & VALIDATION (3001 – 3999) =====
    // ===== ENUM (3001 – 3099) =====
    ENUM_INVALID(3001, "Enum invalid", HttpStatus.BAD_REQUEST),
    USER_ROLE_INVALID(3002, "User role invalid (CUSTOMER, ADMIN)", HttpStatus.BAD_REQUEST),
    USER_STATUS_INVALID(3003, "User status invalid (ACTIVE, BANNED)", HttpStatus.BAD_REQUEST),
    USER_LOGIN_TYPE_INVALID(3004, "User login type invalid (GOOGLE, APP)", HttpStatus.BAD_REQUEST),
    ITEM_STATUS_INVALID(3005, "Item status invalid (ACTIVE, OUT, PAUSED)", HttpStatus.BAD_REQUEST),
    ITEM_TYPE_INVALID(3006, "Item type invalid (MAIN, SIDE, DRINK)", HttpStatus.BAD_REQUEST),
    ORDER_STATUS_INVALID(3007, "Order status invalid ( PENDING, CANCELLED, CONFIRMED, COMPLETED)", HttpStatus.BAD_REQUEST),
    ORDER_PENDING_NO_UPDATE(3008, "Pending can only be initialized.", HttpStatus.BAD_REQUEST),
    ORDER_NO_PENDING(3009, "Status order not PENDING", HttpStatus.BAD_REQUEST),
    ORDER_NO_CONFIRMED(3010, "Status order not CONFIRMED", HttpStatus.BAD_REQUEST),

    // === VALIDATION (3101 – 3999) ===
    EMAIL_INVALID(3101, "Email invalid", HttpStatus.BAD_REQUEST),
    NOT_BLANK(3102, "Value request.{field} is blank", HttpStatus.BAD_REQUEST),
    NOT_NULL(3103, "Value request.{field} is null", HttpStatus.BAD_REQUEST),

    // ===== COMMON (9000 – 9999) =====
    INTERNAL_ERROR(9001, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_FOUND(9002, "Not Found", HttpStatus.INTERNAL_SERVER_ERROR),
    OTHER_ERROR(9999, "Other error", HttpStatus.INTERNAL_SERVER_ERROR);

    int code;
    String message;
    HttpStatusCode httpStatus;
}

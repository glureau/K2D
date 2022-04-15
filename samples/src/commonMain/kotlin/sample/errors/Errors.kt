package sample.errors

enum class PlayerExceptionType {
    AUDIO_FETCHING_ERROR,
    UNEXPECTED_ERROR,
    AUDIO_PLAYBACK_ERROR,
}

enum class PlayerExceptionSubType {
    NONE,
    NETWORK_ERROR,
    DATA_ERROR,
    SERVER_ERROR,
    PARSING_ERROR,
    RESPONSE_ERROR,
    INVALID_RESPONSE_ERROR,
    MEDIA_ERROR,
    TOKEN_ERROR,
    DECRYPTION_ERROR,
    STREAM_BLANK_ERROR,
}

sealed class PlayerException constructor(
    val type: PlayerExceptionType,
    val subtype: PlayerExceptionSubType,
    cause: Exception
) : Exception(cause)

sealed class PlayerAudioFetchingException(type: PlayerExceptionSubType, cause: Exception) :
    PlayerException(PlayerExceptionType.AUDIO_FETCHING_ERROR, type, cause)

enum class NetworkStatus {
    UNAVAILABLE_NETWORK,
    TIMEOUT,
    OTHER,
}

class PlayerNetworkException(val networkStatus: NetworkStatus, cause: Exception) :
    PlayerAudioFetchingException(PlayerExceptionSubType.NETWORK_ERROR, cause)

enum class MissingData {
    NO_URL_FOR_MEDIA_SERVICE,
    NO_LICENSE_TOKEN,
    NO_TRACK_TOKEN,
    NO_FORMAT_REQUESTED,
    UNAVAILABLE_TRACK,
    OTHER,
}

class PlayerDataException(val missingData: MissingData, cause: Exception) :
    PlayerAudioFetchingException(PlayerExceptionSubType.DATA_ERROR, cause)

class PlayerServerException(val httpStatus: String, cause: Exception) :
    PlayerAudioFetchingException(PlayerExceptionSubType.SERVER_ERROR, cause)

enum class ParsingError {
    DOES_NOT_CONFORM_TO_SCHEMA,
    PARSING_ERROR,
}

class PlayerParsingException(val parsingError: ParsingError, cause: Exception) :
    PlayerAudioFetchingException(PlayerExceptionSubType.PARSING_ERROR, cause)

enum class ResponseError {
    API_DEPRECATED,
    INVALID_LICENSE_TOKEN,
    EXPIRED_LICENSE_TOKEN,
    UNAUTHORIZED_LICENSE_TOKEN,
    UNKNOWN_CODE,
}

class PlayerResponseException(val responseError: ResponseError, cause: Exception) :
    PlayerAudioFetchingException(PlayerExceptionSubType.RESPONSE_ERROR, cause)

class PlayerInvalidResponseException(
    val numberOfMediaReceived: Int,
    val numberOfMediaRequested: Int,
    cause: Exception
) : PlayerAudioFetchingException(PlayerExceptionSubType.INVALID_RESPONSE_ERROR, cause)

enum class MediaError {
    NO_FORMAT,
    ALREADY_EXPIRED,
    EMPTY_SOURCE,
}

class PlayerMediaException(val mediaError: MediaError, cause: Exception) :
    PlayerAudioFetchingException(PlayerExceptionSubType.MEDIA_ERROR, cause)

enum class TokenError {
    INVALID_TRACK_TOKEN,
    EXPIRED_TRACK_TOKEN,
    UNAUTHORIZED_TRACK_TOKEN,
    COUNTRY_MISMATCH,
    UNKNOWN_CODE,
}

class PlayerTokenException(val tokenError: TokenError, cause: Exception) :
    PlayerAudioFetchingException(PlayerExceptionSubType.TOKEN_ERROR, cause)

class PlayerUnexpectedException(cause: Exception) :
    PlayerException(PlayerExceptionType.UNEXPECTED_ERROR, PlayerExceptionSubType.NONE, cause)

sealed class PlayerAudioPlaybackException(subType: PlayerExceptionSubType, cause: Exception) :
    PlayerException(PlayerExceptionType.AUDIO_PLAYBACK_ERROR, subType, cause)

class PlayerDecryptionException(cause: Exception) :
    PlayerAudioPlaybackException(PlayerExceptionSubType.DECRYPTION_ERROR, cause)

class PlayerStreamBlankException(cause: Exception) :
    PlayerAudioPlaybackException(PlayerExceptionSubType.STREAM_BLANK_ERROR, cause)

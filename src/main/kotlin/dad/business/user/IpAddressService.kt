package dad.business.user

import javax.servlet.http.HttpServletRequest

class IpAddressService {
    companion object {
        private val IP_HEADER_CANDIDATES = arrayOf(
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
        )

        fun getIpAddressFromRequest(request: HttpServletRequest): String {

            for (header in IP_HEADER_CANDIDATES) {
                val result = request.getHeader(header)

                if (!result.isNullOrEmpty()) {
                    return result
                }
            }

            return request.remoteAddr
        }
    }
}

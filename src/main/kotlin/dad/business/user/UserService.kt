package dad.business.user

import dad.business.data.DATBASE_UTIL
import dad.business.data.component.User

class UserService {
    companion object {
        fun getUser(username: String, password: String?): User? {
            // get the user by username
            val user = DATBASE_UTIL.getUserByUsername(username)
            println(user)
            if (user == null) {
                return null
            } else if (user.password == password) {
                return user
            } else {
                println("incorrect password entered for user $username")
                return null
            }
        }

        fun addLoginAttempt(username: String, password: String?, ipAddress: String, successful: Boolean) {
            DATBASE_UTIL.insertSignInAttempt(username, password, ipAddress, successful)
        }
    }
}

package ie.wit.adventurio.models

interface UserStore {
    fun registerAccount(user: Account)
    fun getAllAccounts(): List<Account>
    fun updateAccount(user: Account)
    fun deleteAccount(user: Account)
}
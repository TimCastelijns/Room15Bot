package bot.usecases

class AcceptUserUseCase : UseCase<String, String> {

    companion object {
        private const val rulesUrl = "http://room-15.github.io/"

        private var acceptMessage = "@%s welcome. Please start by reading the [rules]($rulesUrl) and confirm you have read them before saying anything else."
    }

    override fun execute(params: String) = acceptMessage.format(params.replace(" ", ""))

}

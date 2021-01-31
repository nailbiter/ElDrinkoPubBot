#!/usr/bin/env python
""" generated source for module App """
# package: nl.insomnia247.nailbiter.eldrinkopubbot
import java.io.File

import java.io.FileWriter

import nl.insomnia247.nailbiter.eldrinkopubbot.util.SecureString

import org.apache.commons.io.FileUtils

import org.telegram.telegrambots.ApiContextInitializer

import org.telegram.telegrambots.meta.TelegramBotsApi

import org.telegram.telegrambots.meta.exceptions.TelegramApiException

import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.ElDrinkoPubBot

import org.apache.logging.log4j.Logger

import org.apache.logging.log4j.LogManager

import java.util.Arrays

# 
#  * Hello world!
#  *
#  
class App(object):
    """ generated source for class App """
    _Log = LogManager.getLogger()

    @classmethod
    def main(cls, args):
        """ generated source for method main """
        file_ = File("./secret.txt")
        content = FileUtils.readFileToString(file_, "utf-8").trim()
        commit_data = System.getenv().get("COMMIT_DATA")
        bot_name = System.getenv().getOrDefault("BOT_NAME", "ElDrinkoPubBot")
        fn = String.format(".tmp/%s.runs.txt", bot_name)
        f = File(fn)
        cls._Log.info(SecureString.format("file flag: %s", fn))
        if f.exists():
            raise Exception(String.format("only one instance of \"%s\" allowed to run!", bot_name))
        else:
            cls._Log.info(SecureString.format("save \"%s\" to \"%s\"", file_content, fn))
            w.write(file_content)
            w.flush()
        cls._Log.info(SecureString.format("COMMIT_DATA: %s", commit_data))
        cls._Log.info(SecureString.format("BOT_NAME: %s", bot_name))
        ApiContextInitializer.init()
        telegramBotsApi = TelegramBotsApi()
        edpb = ElDrinkoPubBot(content, commit_data, bot_name)
        Runtime.getRuntime().addShutdownHook(Thread())
        telegramBotsApi.registerBot(edpb)


if __name__ == '__main__':
    import sys
    App.main(sys.argv)


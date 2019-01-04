package com.timcastelijns.room15bot.usecases

import com.timcastelijns.room15bot.bot.usecases.AhmadUseCase
import org.junit.Test
import kotlin.test.assertTrue

class AhmadUseCaseTest {
    private val internships = listOf("Aflac","Caterpillar","Quantum Solutions Corporation","Roland Machinery Co.","State of Illinois","Microsoft","OkCupid","Tinder","Uber","Lyft","Bank of America","Google","Kayak","Cisco","Ubiquity Networks","Snapchat","Facebook","Instagram https://www.instagram.com/rashiq.z/","Twitter","Coinbase","Dell","Deutsche Bank","General Electric","Groupon","Hilton","Honda","HP, Inc.","IBM","JetBlue Airways","Johnson & Johnson","Kroger","L’Oréal","Macy's, Inc.","MetLife","Morgan Stanley","Motorola Solutions, Inc.","NASA","Nascar","Nike","Red Bull","Samsung","Spotify","T-Mobile","The Nature's Bounty Co.","Thomson Reuters","Toyota Motors","Unilever","Warner Music Group","SAP SE","Lenovo","Xiaomi","Sony","Apple","Acer","LG Electronics","Adidas","American Express","Visa","Mastercard","Adobe Systems","The Cheesecake Factory","NVIDIA","Intel","AMD","McDonald's","Burger King","Dropbox","Pagseguro","SpaceX","Nokia","FedEx","AT&T","Siemens","Delta Airlines","American airlines","Kia motors","Ford motors","General Motors","Amazon","Magneto IT Solutions","My Apps Development ","QBurst","Appentus","Trigent","Confianz Global","Endive Software","Wildnet Technologies","Tata Consultancy Services Ltd.","Infosys","ConCur","VMWare","Evernote","PayPal","Blizzard Entertainment","LinkedIn","Oracle","Tata Consultancy Services","Slack Technologies, Inc.","Mailchimp","Namecheap","Square","TeamViewer","Atlassian","Zendesk","MuleSoft","Squarespace","MathWorks","LiveChat","Kahoot!","Deputy","Telegram Messenger","GitHub","JetBrains","Balsamiq","CircleCI","Wondershare","Acronis","Notepad++","Trimble","PayScale","Stack Exchange","Reddit","Foxconn","Alphabet Inc.","Huawei","Hitachi","Panasonic","Accenture","Taiwan Semiconductor Manufacturing","Texas Instruments","Qualcomm","Micron Technology","Nintendo","Broadcom","ASML","Applied Materials","Salesforce","Raspberry Pi Foundation")

    @Test
    fun testRandomAhmadReply() {
        assertTrue { internships.contains(AhmadUseCase().execute(Unit)) }
        assertTrue { internships.contains(AhmadUseCase().execute(Unit)) }
        assertTrue { internships.contains(AhmadUseCase().execute(Unit)) }
        assertTrue { internships.contains(AhmadUseCase().execute(Unit)) }
        assertTrue { internships.contains(AhmadUseCase().execute(Unit)) }
    }
}

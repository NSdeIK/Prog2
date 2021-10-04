
#[NS_DEIK] Projekt

---

* ## NS_KLIENS
  * #### gameroom
    * ##### room_create
      |Működése | Kép |
      |-|-|
      |![](kepek/szoba_letrehozas.png)|![](kepek/jatek_szobakeszites.png)|
    * ##### room_join
      |Működése | Kép |
      |-|-|
      |Játékos beírja a szobakódot, és megnyomja az entert, akkor elküldi az üzenetet a fő szervernek, hogy létezik-e a szobakód, és mi az ip címe a kliens-szervernek?|![](kepek/jatek_join1.png)|
      |Ha sikeres a szobakód, akkor rácsatlakozik a szobára, és megkapom a kliens-szervertől az adatokat (kik vannak bent?)|![](kepek/jatek_join2.png)|
    * Data/DataType/User
      |Működése |
      |-|
      |Röviden: User --> Eltárolja felhasználói adatokat (ip címe, neve, stb.) |
      | Data/DataType --> Üzenetek formája, milyen típusa a csomagnak. Pl. chat típusa --> akkor chat formájában kapom meg az információkat (pl. tartalom) |

  * #### homepage
    * ##### GameController
        |Működése | Kép |
        |-|-|
        |Beírjuk a játékos nevüket, ezután átdobja lobby controllernek|![](kepek/jatek_nev.png)|
    * ##### HomepageController
        |Működése | Kép |
        |-|-|
        |Itt lehet választani a menüt, ha játék gombra megyünk rá (gamecontrollernek dobja át), és így tovább...|![](kepek/kezdolap.png)|
    * ##### SettingsController
        |Működése | Kép |
        |-|-|
        |Itt be lehet állítani a képernyő méretét|![](kepek/beallitasok.png)|
  * #### lobby
    * ##### GameLobbyController
        |Működése | Kép |
        |-|-|
        |Ha sikeres a kapcsolat főszerverrel, akkor kiírja a nevünket...|![](kepek/jatek_lobby.png)|
  * #### mainServer
    * ##### MainServer
        |Működése|
        |-|
        |Főszerver csatlakozása, és egyéb dolgok kezelése |
    * ##### SInterface
  * #### Server
    * DataType,MainData
        |Működése |
      |-|
      |Üzenetek formája, típusa, kommunikálása főszerverrel|
* ## NS_SZERVER
  * Server
      |Működése | Kép |
      |-|-|
      |![](kepek/szerver_mukodese.png)|![](kepek/szerver_on.png)

# `Üdvözöllek az [NS_DEIK] projektén!`

> **Projekt library-t használ ezeket:** (SQLite, SCrypt) 

 1. ## *Projekt célja (Rövid ismertető)*
	 *Szórakoztató jellegű szókincs fejlesztő társasjáték.  
	 Van két különböző nyelv, ami lehetne fejleszteni a szókincsét (Magyar, Angol). 
	 Többféle egyiptomi írásjelekkel lesznek a mezőn, és egyedi dobókockával lehet léptetni az adott mezőre a játékosok[bábuk]. 
	 A játék lényege, hogy az adott mezőn van a játékos, azt alapján kap kártyát, és ki kell találni/kiegészíteni a szót [ha sikerül], akkor átad másik játékosnak[ellefélnek] a bombát, mindaddig fel nem robban az időzített bomba. 
	 Ha egyik játékosnál felrobbant, akkor arra nem jár pont, és akik túlélték, azok megkapják a pontot. Ezáltal lehet fejlődni, és gyűjteni pontokat. Itt nincs se győztes, se vesztes a játék végén. 
	 Pontokat lehet kiváltani valamiféle fejlesztéseken (<- Ideiglenes terv) .*  
 
 2. ## Jelenlegi verzió
	Kliens - v0.8
	Szerver - v0.4
 3. ## Használata [Telepítési tutorial]
	- Szükségünk lesz hozzá **IntelliJ IDEA**-ra
	- Java verzió: **16-17**
	- Library: **openjfx**, **SQLite ** , **SCrypt**
	- IntelliJ IDEA --> [ **File --> Settings --> Plugins --> JavaFX Runtime for plugins** ]
	-    --> ***Projekt névre jobb click --> Open Module Settings --> Libraries --> Plus --> openjfx-sdk [összes .jar*]***
	- IP címek konfigurálása -> Kliens IP cím, Szerver IP cím
	 Futtatása
		- Kliens -> Run
		- Szerver -> Run -> On/Off

 4. ## Tervek

	 - Kliens
		 - Játék
			 - Szókincsfejlesztő  **40%**
				 - Magyar - SQL **80%**
				 - Angol - SQL **0%**
			- Memóriafejlesztő [csak 2 játékos --> Ideiglenes terv] **0%**
		 - Beállítások
			 - Képernyő méret - **15%**
			 - Hangok - **0%**
		 - Felhasználónév [Játékos]
    		 - Felhasználónév rendszer **70%**
			 - Felh./Jelszó rendszer **50%**
				 - Létezik-e? Ellenőrzések **0%**
				 - XP rendszer **0%**
				 - Pont rendszer **10%**
		 - Szoba + Chat
			 - Létrehozás **50%**
				 - Szobakód generálása **100%**
				 - Játékmód választás **0%**
				 - Játékosok kirúgása/bannolása **0%**
				 - Játék elindítása [xy mennyiség játékos] **0%**
				 - Chat **85%**
			 - Csatlakozás
				 - Szobakód keresgélése --> Melyik IP címen van? **100%**
				 - Chat **85%**
				 - Készenállás/Ready funkció  **100%**
		 - Lobby
			 - Chat **85%**
			 - Profil megtekintése **0%**
			 - Shop rendszer (pontok) [Ideglenes terv] **0%**
	 - Szerver
		 - Szobalétrehozás --> Kliens lesz majd a szerver **85%**
		 - Figyelés [ki merre hol van...] **0%**
		 - Szobakód tárolása [kié ez a kód, ez az ip címé...] **100%**
		 - SQL biztosítása **50%**
		 - Játék kérések biztosítása **0%**
		 
> **Megjegyzés:** Bővűlni fog majd a lista, és jelenlegi fejlesztések százalékkal lesz mérve.
1. ## Dokumentációk
   > [Dokumentáció](https://github.com/NSdeIK/Prog2/blob/main/Dokumentacio/Dokumentaciok.md)



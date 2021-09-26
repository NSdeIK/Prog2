# `Üdvözöllek az [NS_DEIK] projektén!`

 1. ## *Projekt célja (Rövid ismertető)*
	 *Szórakoztató jellegű szókincs fejlesztő társasjáték.  
	 Van két különböző nyelv, ami lehetne fejleszteni a szókincsét (Magyar, Angol). 
	 Többféle egyiptomi írásjelekkel lesznek a mezőn, és egyedi dobókockával lehet léptetni az adott mezőre a játékosok[bábuk]. 
	 A játék lényege, hogy az adott mezőn van a játékos, azt alapján kap kártyát, és ki kell találni/kiegészíteni a szót [ha sikerül], akkor átad másik játékosnak[ellefélnek] a bombát, mindaddig fel nem robban az időzített bomba. 
	 Ha egyik játékosnál felrobbant, akkor arra nem jár pont, és akik túlélték, azok megkapják a pontot. Ezáltal lehet fejlődni, és gyűjteni pontokat. Itt nincs se győztes, se vesztes a játék végén. 
	 Pontokat lehet kiváltani valamiféle fejlesztéseken (<- Ideiglenes terv) .*  
 
 2. ## Jelenlegi verzió
	Kliens - v0.03
	Szerver - v0.01
 3. ## Használata [Telepítési tutorial]
	- Szükségünk lesz hozzá **IntelliJ IDEA**-ra
	- Java verzió: **16-17**
	- Library: **openjfx**
	- IntelliJ IDEA --> [ **File --> Settings --> Plugins --> JavaFX Runtime for plugins** ]
	-    --> ***Projekt névre jobb click --> Open Module Settings --> Libraries --> Plus --> openjfx-sdk [összes .jar*]***
	- IP címek konfigurálása -> Kliens IP cím, Szerver IP cím
	 Futtatása
		- Kliens -> Run
		- Szerver -> Run -> On/Off

 4. ## Tervek

	 - Kliens
		 - Játék
			 - Szókincsfejlesztő
				 - Magyar - SQL
				 - Angol - SQL
			- Memóriafejlesztő [csak 2 játékos --> Ideiglenes terv]
		 - Beállítások
			 - Képernyő méret
			 - Hangok
		 - Felhasználónév [Játékos]
			 - Felh./Jelszó rendszer
				 - Létezik-e? Ellenőrzések
				 - XP rendszer
				 - Pont rendszer
		 - Szoba + Chat
			 - Létrehozás
				 - Szobakód generálása
				 - Játékmód választás
				 - Játékosok kirúgása/bannolása
				 - Játék elindítása [xy mennyiség játékos]
				 - Chat
			 - Csatlakozás
				 - Szobakód keresgélése --> Melyik IP címen van?
				 - Chat
				 - Készenállás/Ready funkció 
		 - Lobby
			 - Chat
			 - Profil megtekintése
			 - Shop rendszer (pontok) [Ideglenes terv]
	 - Szerver
		 - Szobalétrehozás --> Kliens lesz majd a szerver
		 - Figyelés [ki merre hol van...]
		 - Szobakód tárolása [kié ez a kód, ez az ip címé...]
		 - SQL biztosítása
		 - Játék kérések biztosítása
		 
> **Megjegyzés:** Bővűlni fog majd a lista, és jelenlegi fejlesztések százalékkal lesz mérve.
5. ## Dokumentációk
		Hamarosan...



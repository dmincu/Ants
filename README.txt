Lucky Strike
Patrascoiu Andrei Radu 321CB
Bejgu Andreea 321CB
Mihalcea Ioan Serban 321CB
Mincu Diana 321CB

Etapa 1

Continut:
Arhiva contine proiectul Eclipse in care a fost dezvoltat programul

Contributii:
Bejgu Andreea: implementarea algoritmului Lee si mentinere directie
Mincu Diana: implementarea algoritmului Lee si A*
Patrascoiu Andrei, Mihalcea Serban: analiza metode de a alege destinatii pentru
furnicile care nu cauta mancare

Clase:
Botul a fost dezvoltat in Java, pornind de la StarterBotul oferit de echipa PA.
Au fost folosite toate clasele date initial
Clasa Sort contine o metoda de sortare pe baza distantei Manhattan

Clasa MyBot contine Botul efectiv, ea continad metoda runTests care apeleaza 2 metode:
gatherFood si scout

gatherFood: este folosita pentru colectarea mancarii. Pentru fiecare casuta care contine mancare
se gaseste furnica care poate ajunge acolo cel mai repede folosind algoritmul lui Lee de solutionare
a labirintelor, bazat pe o cautare BSF. Acea furnica este trimisa spre mancare si se trece la 
urmatoarea casuta cu mancare.

scout: trimite furnicile care nu cauta mancare sa exploreze harta. In aceasta versiune ele sunt 
trimise spre diferite puncte de pe marginea hartii. Traseul este calculat folosind algoritmul 
A*

Algoritmi:

Principalul algoritm folosit a fost cel al lui Lee. Acesta are complexitatea O(m*n), dar cand este
folosit pentru a cauta mancare complexitatea devine constanta (deoarece se cauta doar in zona de
vizibilitate a unei furnici care este viewradius, iar m*n este viewradius^2, care este 77 tot timpul)
Algoritmul de gasire a destinatiilor ruleaza in timp constant pentru o furnica, iar algoritmul A*
are complexitatea O(n*m), desi are imbunatatiri fata de un algoritm BSF obisnuit

Functia de mentinere a destinatiei ruleaza in timp O(n), unde n este numarul furnicilor

Pentru etapele urmatoare ne propunem o modalitate mai buna de gasire a destinatiilor bazata pe vizitarea 
zonelor nevizitate sau care au fost vizitate cu mai mult timp in urma precum si o metoda de trimitere a 
unor grupuri de furnici pentru a distruge musuroaiele inamice

Surse de inspiratie: 

strategia generala a jucatorului xathis, din competitia AIChallenge: 
http://xathis.com/posts/ai-challenge-2011-ants.html

Algoritmul lui Lee: www.ece.northwestern.edu/~haizhou/357/lec6.pdf

A*: http://www.policyalmanac.org/games/heuristics.htm




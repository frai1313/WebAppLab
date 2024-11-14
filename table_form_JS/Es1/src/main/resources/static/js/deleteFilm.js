
// DELETE ROW ON DEMAND
document.getElementById("removeFilmButton").addEventListener("click", function() {
    document.getElementById("removeFilmForm").style.display = "block";
    document.getElementById("filmForm").style.display = "none";
});

// Funzione per rimuovere il film in base al titolo
function removeFilmByTitle(event) {
    event.preventDefault(); // Impedisce il submit del form

    // Recupera il numero del film da rimuovere
    const filmTitle = document.getElementById("filmTitle").value.trim().toLowerCase();


    // Ottiene tutte le righe della tabella, escluse le intestazioni
    const rows =document.getElementById('filmTable').querySelectorAll('tr');
    let filmFound = false; // Flag per verificare se il film è stato trovato

    // Cicla tutte le righe per trovare quella con il titolo corrispondente
    rows.forEach(row => {
        const titleCell = row.cells[0].textContent.trim().toLowerCase();

        // Confronta il titolo inserito con quello della riga
        if (titleCell === filmTitle) {
            row.remove(); // Rimuove la riga corrispondente
            filmFound = true;
        }
    });

    if (filmFound) {
        document.getElementById("removeFilmForm").style.display = "none"; // Nasconde il form
        document.getElementById("removeFilmByTitleForm").reset(); // Resetta il campo del form
    } else {
        alert("Film title not found!"); // Avviso per titolo non trovato
    }
}

// DELETE specific row
// Aggiunge la funzionalità di eliminazione per i pulsanti "Elimina" già presenti
document.querySelectorAll(".deleteButton").forEach(button => {
    button.addEventListener("click", function() {
        const row = button.closest("tr");
        row.parentNode.removeChild(row); // Elimina la riga
    });
});


// Funzione per eliminare la riga con opzioni diverse
function deleteRow(button) {
    const row = button.parentNode.parentNode; // Trova la riga corrente

    // Opzione 1: Rimuove la riga direttamente
    row.parentNode.removeChild(row);

}

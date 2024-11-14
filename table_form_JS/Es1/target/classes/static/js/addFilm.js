


console.log('HELLO JS');


document.getElementById("addFilmButton").addEventListener("click", function() {
    document.getElementById("filmForm").style.display = "block";
    document.getElementById("removeFilmForm").style.display = "none";
});

document.getElementById("newFilmForm").addEventListener("submit", function(event) {
    // Impedisce il comportamento di submit predefinito (invio dei dati)
    event.preventDefault();

    // Recupera i valori inseriti dall'utente nei campi del form
    const title = document.getElementById("title").value;
    const year = document.getElementById("year").value;
    const director = document.getElementById("director").value;
    const genre = document.getElementById("genre").value;
    const awards = document.getElementById("awards").value;

    // Riferimento alla tabella dove verranno aggiunti i nuovi film
    const tableBody = document.getElementById("filmTable").getElementsByTagName('tbody')[0];

    addNewRowV1(tableBody , title , year , director, genre, awards);

    // Nasconde il form di aggiunta del film dopo l'inserimento
    document.getElementById("filmForm").style.display = "none";
    // Reset dei campi del form per future aggiunte
    document.getElementById("newFilmForm").reset();

});

function addNewRowV1(tableBody , title , year , director, genre, awards){
    // Crea una stringa HTML per la nuova riga
    const newRowHTML = `
        <tr>
            <td>${title}</td>
            <td><span class="badge bg-secondary">${year}</span></td>
            <td>${director}</td>
            <td>${genre}</td>
            <td>${awards}</td>
            <td><button class="btn btn-danger btn-sm deleteButton"><i class="fa-solid fa-trash"></i></button></td>
        </tr>
    `;

    // Aggiunge la nuova riga alla tabella usando innerHTML
    tableBody.innerHTML += newRowHTML;

    console.log('Adding new Row');
}

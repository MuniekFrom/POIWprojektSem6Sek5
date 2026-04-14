function getAvailableSlots() {

    const doctorId = document.getElementById('doctorId').value;
    const resultsContainer = document.getElementById('results');


    resultsContainer.innerHTML = 'Ładowanie...';


    fetch(`/appointments/available?doctorId=${doctorId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Błąd sieci lub serwera');
            }
            return response.json();
        })
        .then(slots => {

            resultsContainer.innerHTML = '';

            if (slots.length === 0) {
                resultsContainer.innerHTML = '<p>Brak wolnych terminów dla tego lekarza.</p>';
                return;
            }


            slots.forEach(slot => {
                const slotDiv = document.createElement('div');
                slotDiv.className = 'slot';

                slotDiv.innerHTML = `
                    <strong>Data i czas:</strong> ${slot.startTime} <br>
                    <button onclick="alert('Tu dodamy logikę rezerwacji dla slotu ID: ${slot.id}')">Zarezerwuj ten termin</button>
                `;
                resultsContainer.appendChild(slotDiv);
            });
        })
        .catch(error => {
            console.error('Błąd:', error);
            resultsContainer.innerHTML = '<p style="color:red;">Wystąpił błąd podczas pobierania danych.</p>';
        });
}
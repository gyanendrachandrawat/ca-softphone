function createTableFromJson(jsonData,elementId) {
  // Get the reference to the table element in the HTML
  const table = document.getElementById(elementId);

  // Create the table header row
  const thead = document.createElement('thead');
  const headerRow = document.createElement('tr');

  // Create table header cells based on the keys of the first object in the JSON array
  Object.keys(jsonData[0]).forEach(key => {
    const th = document.createElement('th');
    th.textContent = key;
    headerRow.appendChild(th);
  });

  thead.appendChild(headerRow);
  table.appendChild(thead);

  // Create the table body rows
  const tbody = document.createElement('tbody');

  jsonData.forEach(obj => {
    const row = document.createElement('tr');

    // Create table cells and populate them with the values from each object
    Object.values(obj).forEach(value => {
      const cell = document.createElement('td');
      cell.textContent = value;
      row.appendChild(cell);
    });

    tbody.appendChild(row);
  });
  $("#message-table").empty();

  table.appendChild(tbody);
}
$(function () {
  $("#button-refresh").click(function () {
    console.log("called")
    var loader = $('#loader');
    loader.show();

    var inputValue = document.getElementById('myInput').value;
    $.getJSON('/sms/list?from=' + inputValue).then(function (data) {
      createTableFromJson(data,'message-table');
    })
    loader.hide();
  })
});
$(function () {
  $("#button-refresh-call").click(function () {
    console.log("called")
    var loader = $('#loader');
    loader.show();

    var inputValue = document.getElementById('myInput').value;
    $.getJSON('/call/logs?from=' + inputValue).then(function (data) {
      createTableFromJson(data,'call-logs-table');
    })
    loader.hide();
  })
});
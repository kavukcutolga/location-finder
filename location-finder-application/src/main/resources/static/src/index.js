let map;
let counter = 1;
let initialLatitude = '37.779325';
let initialLongitude = '-122.426856'; // Location of San Francisco center.
const bingApiKey = '';
function initialize() {
    const observable = rxjs.Observable.create(observer => {
        const eventSource = new EventSource('/location?longitude=' + initialLongitude + '&latitude=' + initialLatitude + '&type=FoodTruck');
        eventSource.onmessage = x => observer.next(x.data);
        eventSource.onerror = x => observer.error(x);

        return () => {
            eventSource.close();
        };
    });

    const subscription = observable.subscribe({
        next: data => {
            const locationItem = JSON.parse(data);
            //Create custom Pushpin
            var pin = new Microsoft.Maps.Pushpin(new Microsoft.Maps.Location(locationItem.latitude, locationItem.longitude), {
                title: locationItem.name,
                text: counter
            });
            counter ++;
            //Add the pushpin to the map
            map.entities.push(pin);
        },
        error: err => {if(!err.returnValue){ console.log(err) }},
        complete() {
            console.log('done');
        }
    });
}

function GetMap() {
    document.getElementById("longitude").value = initialLongitude;
    document.getElementById("latitude").value = initialLatitude;
    showInMap();
}

function showInMap() {
    document.getElementById('loading-icon').style.display = "none";
    map = new Microsoft.Maps.Map('#myMap', {
        credentials: bingApiKey,
        center: new Microsoft.Maps.Location(initialLatitude,initialLongitude),
        zoom: 15
    });
    Microsoft.Maps.Events.addHandler(map, 'viewchangeend',  () => {
       initialLatitude = map.getCenter().latitude;
       initialLongitude = map.getCenter().longitude;
       document.getElementById("longitude").value = initialLongitude;
       document.getElementById("latitude").value = initialLatitude;
       map.entities.clear();
       initialize();
    });
}
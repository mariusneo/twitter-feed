
$(document).ready(function() {
    var since_id = 0;
    if ($('#tweetsList').children().length > 0)
        since_id = $('#tweetsList').children(":first").attr("data-tweet-id")

    $('#refresh').click(function() {
        var params = {
            since_id: since_id,
        };
        // alert(jQuery.param(params));
        getLatestTweets(params);
    });
});


function getLatestTweets(query) {
    $.ajax({
        url: serviceApiUrl + "/tweets/latest?" + jQuery.param(query),
        dataType: 'jsonp',
        success: function(data) {
            var tweets = $('#tweetsList');
            for (index = 0, len = data.length; index < len; ++index) {
                console.log(data[index].text);
            }
        }
    });
}
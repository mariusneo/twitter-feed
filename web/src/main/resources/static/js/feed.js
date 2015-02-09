
$(document).ready(function() {
    $('#refresh').click(function() {
        var since_id = 0;
        if ($('#tweetsList').children().length > 0)
            since_id = $('#tweetsList').children(":first").attr("data-tweet-id")

        var params = {
            since_id: since_id,
        };
        getLatestTweets(params);
    });

    var checker = setInterval(
        function(){
            checkForTweetUpdates(function(){clearTimeout(checker);})
        }, 5000);



});

function checkForTweetUpdates(clearIntervalCallback){
    var since_id = 0;
    if ($('#tweetsList').children().length > 0)
        since_id = $('#tweetsList').children(":first").attr("data-tweet-id")
    var query = {
            since_id: since_id,
    };
    $.ajax({
        url: serviceApiUrl + "/tweets/updatestatus?" + jQuery.param(query),
        dataType: 'jsonp',
        success: function(updateStatus) {
            if (updateStatus.available){
                $('#notificationBar').addClass("active");
                clearIntervalCallback();
            }
        }
    });
}


function getLatestTweets(query) {
    $.ajax({
        url: serviceApiUrl + "/tweets/latest?" + jQuery.param(query),
        dataType: 'jsonp',
        success: function(tweets) {
            var tweetsContainer = $('#tweetsList');
            for (index = tweets.length-1, len = tweets.length; index >= 0; --index) {
                var newTweetElement = "<li class='tweet active' data-tweet-id='"+tweets[index].id+"'>"
                    .concat("<img src='"+tweets[index].user.profileImageUrl+"' class='avatar'/>")
                    .concat("<blockquote>")
                    .concat("<cite>"+tweets[index].user.name+"</cite>")
                    .concat("<span class='content'>"+tweets[index].text+"</span>")
                    .concat("</blockquote>")
                    .concat("</li>");
                tweetsContainer.prepend(newTweetElement)
            }

            $('#notificationBar').removeClass("active");
            var checker = setInterval(
                            function(){
                                checkForTweetUpdates(function(){clearInterval(checker);})
                            }, 5000);
        }
    });
}
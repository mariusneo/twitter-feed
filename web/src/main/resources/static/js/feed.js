var feed = {
    state : {paging:false},

    handleEvent: function(e) {
        switch(e.type) {
            case "scroll":
                this.checkWindowScroll();
                break;
        }
    },

    // Method to check if more tweets should be loaded, by scroll position
    checkWindowScroll : function(){
        // Get scroll pos & window data
        var h = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);
        var s = (document.body.scrollTop || document.documentElement.scrollTop || 0);
        var scrolled = (h + s) > document.body.offsetHeight;

        // If scrolled enough, not currently paging and not complete...
        if(scrolled && !this.state.paging) {

          // Set application state (Paging)
          this.state.paging = true;

          // Get the next page of tweets from the server
          this.getPreviousTweets();
        }
    },

    getPreviousTweets : function() {
        var max_id = 0;
        if ($('#tweetsList').children().length > 0)
            max_id = $('#tweetsList').children(":last-child").attr("data-tweet-id")

        var query = {
            max_id: max_id,
        };

        var self = this; // save object reference
        $('#loader').addClass("active");
        // simulate a small delay in order to show the loading image - UI sugar
        setTimeout(function(){
            $.ajax({
                url: serviceApiUrl + "/tweets/previous?" + jQuery.param(query),
                dataType: 'jsonp',
                success: function(tweets) {
                    var tweetsContainer = $('#tweetsList');
                    for (index = tweets.length-1, len = tweets.length; index >= 0; --index) {
                        var tweetHtml = self.createTweetHtml(tweets[index]);
                        tweetsContainer.append(tweetHtml);
                    }

                    $('#loader').removeClass("active");
                    self.state.paging = false;
                }
            });
        }, 1000);
    },

    checkForTweetUpdates : function(clearIntervalCallback){
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
    },

    createTweetHtml : function(tweet){
        var newTweetElement = "<li class='tweet active' data-tweet-id='"+tweet.id+"'>"
            .concat("<img src='"+tweet.user.profileImageUrl+"' class='avatar'/>")
            .concat("<blockquote>")
            .concat("<cite>"+tweet.user.name+"</cite>")
            .concat("<span class='content'>"+tweet.text+"</span>")
            .concat("</blockquote>")
            .concat("</li>");
        return newTweetElement;
    },

    getLatestTweets : function(query) {
        var self = this;
        $.ajax({
            url: serviceApiUrl + "/tweets/latest?" + jQuery.param(query),
            dataType: 'jsonp',
            success: function(tweets) {
                var tweetsContainer = $('#tweetsList');
                for (index = tweets.length-1, len = tweets.length; index >= 0; --index) {
                    var tweetHtml = self.createTweetHtml(tweets[index]);
                    tweetsContainer.prepend(tweetHtml);
                }

                $('#notificationBar').removeClass("active");
                var checker = setInterval(
                                function(){
                                    self.checkForTweetUpdates(function(){clearInterval(checker);})
                                }, 5000);
            }
        });
    }
};


$(document).ready(function() {
    $('#refresh').click(function() {
        var since_id = 0;
        if ($('#tweetsList').children().length > 0)
            since_id = $('#tweetsList').children(":first").attr("data-tweet-id")

        var params = {
            since_id: since_id,
        };
        feed.getLatestTweets(params);
    });

    var checker = setInterval(
        function(){
            feed.checkForTweetUpdates(function(){clearTimeout(checker);})
        }, 5000);


    // Attach scroll event to the window for infinity paging
    window.addEventListener('scroll', feed);
});
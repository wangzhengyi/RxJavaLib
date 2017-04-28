package rxjava.android.com.rxjavastudy.chapter8.stackexchange.models.UserResponse;

import java.util.List;

public class UserResponse {

    /**
     * items : [{"badge_counts":{"bronze":7741,"silver":6906,"gold":545},"account_id":11683,"is_employee":false,"last_modified_date":1493206176,"last_access_date":1493205320,"age":40,"reputation_change_year":25001,"reputation_change_quarter":5739,"reputation_change_month":5739,"reputation_change_week":935,"reputation_change_day":260,"reputation":942345,"creation_date":1222430705,"user_type":"registered","user_id":22656,"accept_rate":89,"location":"Reading, United Kingdom","website_url":"http://csharpindepth.com","link":"http://stackoverflow.com/users/22656/jon-skeet","profile_image":"https://www.gravatar.com/avatar/6d8ebb117e8d83d74ea95fbdd0f87e13?s=128&d=identicon&r=PG","display_name":"Jon Skeet"},{"badge_counts":{"bronze":2482,"silver":2669,"gold":178},"account_id":14332,"is_employee":false,"last_modified_date":1492987884,"last_access_date":1493206674,"age":35,"reputation_change_year":24317,"reputation_change_quarter":4963,"reputation_change_month":4963,"reputation_change_week":805,"reputation_change_day":100,"reputation":729078,"creation_date":1224432467,"user_type":"registered","user_id":29407,"accept_rate":89,"location":"Sofia, Bulgaria","website_url":"http://stackoverflow.com/search?q=user%3a29407&tab=newest","link":"http://stackoverflow.com/users/29407/darin-dimitrov","profile_image":"https://www.gravatar.com/avatar/e3a181e9cdd4757a8b416d93878770c5?s=128&d=identicon&r=PG","display_name":"Darin Dimitrov"},{"badge_counts":{"bronze":2850,"silver":2666,"gold":236},"account_id":52822,"is_employee":false,"last_modified_date":1493158691,"last_access_date":1493203311,"age":39,"reputation_change_year":23854,"reputation_change_quarter":5057,"reputation_change_month":5057,"reputation_change_week":786,"reputation_change_day":200,"reputation":722834,"creation_date":1250527322,"user_type":"registered","user_id":157882,"accept_rate":93,"location":"Amsterdam, Netherlands","website_url":"http://balusc.omnifaces.org","link":"http://stackoverflow.com/users/157882/balusc","profile_image":"https://www.gravatar.com/avatar/89927e2f4bde24991649b353a37678b9?s=128&d=identicon&r=PG","display_name":"BalusC"},{"badge_counts":{"bronze":1708,"silver":1031,"gold":89},"account_id":9266,"is_employee":false,"last_modified_date":1493162102,"last_access_date":1493206665,"reputation_change_year":22444,"reputation_change_quarter":4489,"reputation_change_month":4489,"reputation_change_week":689,"reputation_change_day":147,"reputation":690872,"creation_date":1221698729,"user_type":"registered","user_id":17034,"location":"Madison, WI","link":"http://stackoverflow.com/users/17034/hans-passant","profile_image":"https://i.stack.imgur.com/Cii6b.png?s=128&g=1","display_name":"Hans Passant"},{"badge_counts":{"bronze":2125,"silver":1991,"gold":217},"account_id":4243,"is_employee":false,"last_modified_date":1493206719,"last_access_date":1493206326,"age":46,"reputation_change_year":29115,"reputation_change_quarter":6198,"reputation_change_month":6198,"reputation_change_week":960,"reputation_change_day":315,"reputation":674929,"creation_date":1221344553,"user_type":"registered","user_id":6309,"accept_rate":100,"location":"France","website_url":"http://careers.stackoverflow.com/vonc","link":"http://stackoverflow.com/users/6309/vonc","profile_image":"https://www.gravatar.com/avatar/7aa22372b695ed2b26052c340f9097eb?s=128&d=identicon&r=PG","display_name":"VonC"},{"badge_counts":{"bronze":2307,"silver":1851,"gold":160},"account_id":11975,"is_employee":true,"last_modified_date":1492777083,"last_access_date":1493204995,"age":38,"reputation_change_year":20600,"reputation_change_quarter":4673,"reputation_change_month":4673,"reputation_change_week":585,"reputation_change_day":80,"reputation":663059,"creation_date":1222667162,"user_type":"moderator","user_id":23354,"accept_rate":100,"location":"Forest of Dean, United Kingdom","website_url":"http://blog.marcgravell.com","link":"http://stackoverflow.com/users/23354/marc-gravell","profile_image":"https://i.stack.imgur.com/NJcqr.png?s=128&g=1","display_name":"Marc Gravell"},{"badge_counts":{"bronze":1596,"silver":1544,"gold":100},"account_id":39846,"is_employee":false,"last_modified_date":1493185827,"last_access_date":1493207027,"reputation_change_year":26620,"reputation_change_quarter":6129,"reputation_change_month":6129,"reputation_change_week":922,"reputation_change_day":196,"reputation":638309,"creation_date":1243786808,"user_type":"registered","user_id":115145,"accept_rate":84,"location":"Hiding","website_url":"https://commonsware.com","link":"http://stackoverflow.com/users/115145/commonsware","profile_image":"https://i.stack.imgur.com/wDnd8.png?s=128&g=1","display_name":"CommonsWare"},{"badge_counts":{"bronze":1583,"silver":1435,"gold":111},"account_id":15988,"is_employee":false,"last_modified_date":1492473068,"last_access_date":1493173511,"age":26,"reputation_change_year":20561,"reputation_change_quarter":4496,"reputation_change_month":4496,"reputation_change_week":648,"reputation_change_day":150,"reputation":574642,"creation_date":1225829805,"user_type":"registered","user_id":34397,"accept_rate":87,"location":"New Jersey","website_url":"http://SLaks.net","link":"http://stackoverflow.com/users/34397/slaks","profile_image":"https://www.gravatar.com/avatar/7deca8ec973c3c0875e9a36e1e3e2c44?s=128&d=identicon&r=PG","display_name":"SLaks"},{"badge_counts":{"bronze":1669,"silver":1593,"gold":84},"account_id":35417,"is_employee":false,"last_modified_date":1493203591,"last_access_date":1493206382,"age":44,"reputation_change_year":28765,"reputation_change_quarter":6732,"reputation_change_month":6732,"reputation_change_week":980,"reputation_change_day":215,"reputation":548607,"creation_date":1241362437,"user_type":"moderator","user_id":100297,"location":"Cambridge, United Kingdom","website_url":"http://www.zopatista.com/","link":"http://stackoverflow.com/users/100297/martijn-pieters","profile_image":"https://www.gravatar.com/avatar/24780fb6df85a943c7aea0402c843737?s=128&d=identicon&r=PG","display_name":"Martijn Pieters"},{"badge_counts":{"bronze":1077,"silver":916,"gold":115},"account_id":680,"is_employee":false,"last_modified_date":1493024368,"last_access_date":1493172419,"age":47,"reputation_change_year":22584,"reputation_change_quarter":4896,"reputation_change_month":4896,"reputation_change_week":800,"reputation_change_day":200,"reputation":547941,"creation_date":1218356820,"user_type":"registered","user_id":893,"accept_rate":85,"location":"Christchurch, New Zealand","website_url":"http://hewgill.com","link":"http://stackoverflow.com/users/893/greg-hewgill","profile_image":"https://www.gravatar.com/avatar/747ffa5da3538e66840ebc0548b8fd58?s=128&d=identicon&r=PG","display_name":"Greg Hewgill"},{"badge_counts":{"bronze":255,"silver":175,"gold":23},"account_id":1165580,"is_employee":false,"last_modified_date":1493120925,"last_access_date":1493206624,"reputation_change_year":42275,"reputation_change_quarter":9680,"reputation_change_month":9680,"reputation_change_week":1283,"reputation_change_day":200,"reputation":535728,"creation_date":1326311637,"user_type":"registered","user_id":1144035,"location":"New York, United States","website_url":"http://www.data-miners.com","link":"http://stackoverflow.com/users/1144035/gordon-linoff","profile_image":"https://www.gravatar.com/avatar/e514b017977ebf742a418cac697d8996?s=128&d=identicon&r=PG","display_name":"Gordon Linoff"},{"badge_counts":{"bronze":1005,"silver":877,"gold":92},"account_id":52616,"is_employee":false,"last_modified_date":1493161066,"last_access_date":1493204710,"reputation_change_year":30301,"reputation_change_quarter":7093,"reputation_change_month":7093,"reputation_change_week":979,"reputation_change_day":169,"reputation":533356,"creation_date":1250420422,"user_type":"registered","user_id":157247,"accept_rate":89,"location":"United Kingdom","website_url":"http://www.farsightsoftware.com","link":"http://stackoverflow.com/users/157247/t-j-crowder","profile_image":"https://www.gravatar.com/avatar/ca3e484c121268e4c8302616b2395eb9?s=128&d=identicon&r=PG","display_name":"T.J. Crowder"},{"badge_counts":{"bronze":863,"silver":698,"gold":57},"account_id":10162,"is_employee":false,"last_modified_date":1493204198,"last_access_date":1493205109,"reputation_change_year":22324,"reputation_change_quarter":4120,"reputation_change_month":4120,"reputation_change_week":489,"reputation_change_day":30,"reputation":527186,"creation_date":1221842906,"user_type":"registered","user_id":19068,"location":"United Kingdom","website_url":"","link":"http://stackoverflow.com/users/19068/quentin","profile_image":"https://www.gravatar.com/avatar/1d2d3229ed1961d2bd81853242493247?s=128&d=identicon&r=PG","display_name":"Quentin"},{"badge_counts":{"bronze":1499,"silver":1049,"gold":127},"account_id":8291,"is_employee":false,"last_modified_date":1493024368,"last_access_date":1493197752,"reputation_change_year":20576,"reputation_change_quarter":4231,"reputation_change_month":4231,"reputation_change_week":590,"reputation_change_day":80,"reputation":523987,"creation_date":1221622605,"user_type":"registered","user_id":14860,"accept_rate":98,"website_url":"","link":"http://stackoverflow.com/users/14860/paxdiablo","profile_image":"https://i.stack.imgur.com/vXG1F.png?s=128&g=1","display_name":"paxdiablo"},{"badge_counts":{"bronze":1188,"silver":916,"gold":94},"account_id":34048,"is_employee":false,"last_modified_date":1491432561,"last_access_date":1491829200,"age":61,"reputation_change_year":20453,"reputation_change_quarter":4357,"reputation_change_month":4357,"reputation_change_week":570,"reputation_change_day":70,"reputation":511682,"creation_date":1240625354,"user_type":"registered","user_id":95810,"accept_rate":80,"location":"Sunnyvale, CA","website_url":"http://www.aleax.it","link":"http://stackoverflow.com/users/95810/alex-martelli","profile_image":"https://www.gravatar.com/avatar/e8d5fe90f1fe2148bf130cccd4dc311c?s=128&d=identicon&r=PG","display_name":"Alex Martelli"},{"badge_counts":{"bronze":925,"silver":560,"gold":45},"account_id":134022,"is_employee":false,"last_modified_date":1492972556,"last_access_date":1493207209,"age":46,"reputation_change_year":24985,"reputation_change_quarter":5530,"reputation_change_month":5530,"reputation_change_week":495,"reputation_change_day":60,"reputation":496910,"creation_date":1273269250,"user_type":"registered","user_id":335858,"accept_rate":78,"location":"United States","website_url":"http://stackoverflow.com/users/335858/dasblinkenlight","link":"http://stackoverflow.com/users/335858/dasblinkenlight","profile_image":"https://www.gravatar.com/avatar/4af3541c00d591e9a518b9c0b3b1190a?s=128&d=identicon&r=PG","display_name":"dasblinkenlight"},{"badge_counts":{"bronze":779,"silver":781,"gold":134},"account_id":3748,"is_employee":false,"last_modified_date":1490107058,"last_access_date":1492018126,"age":32,"reputation_change_year":20004,"reputation_change_quarter":4270,"reputation_change_month":4270,"reputation_change_week":620,"reputation_change_day":80,"reputation":486920,"creation_date":1220976258,"user_type":"registered","user_id":5445,"location":"Guatemala","website_url":"http://codingspot.com","link":"http://stackoverflow.com/users/5445/cms","profile_image":"https://www.gravatar.com/avatar/932fb89b9d4049cec5cba357bf0ae388?s=128&d=identicon&r=PG","display_name":"CMS"},{"badge_counts":{"bronze":1104,"silver":936,"gold":103},"account_id":7633,"is_employee":false,"last_modified_date":1493090418,"last_access_date":1493205640,"age":51,"reputation_change_year":18045,"reputation_change_quarter":3896,"reputation_change_month":3896,"reputation_change_week":552,"reputation_change_day":80,"reputation":482482,"creation_date":1221588555,"user_type":"registered","user_id":13302,"accept_rate":99,"location":"Bern, Switzerland","website_url":"","link":"http://stackoverflow.com/users/13302/marc-s","profile_image":"https://www.gravatar.com/avatar/b4779212f57ff2e9549ea90a4499c2d7?s=128&d=identicon&r=PG","display_name":"marc_s"},{"badge_counts":{"bronze":1266,"silver":959,"gold":90},"account_id":11948,"is_employee":false,"last_modified_date":1493132661,"last_access_date":1492813739,"age":36,"reputation_change_year":17389,"reputation_change_quarter":3559,"reputation_change_month":3559,"reputation_change_week":560,"reputation_change_day":130,"reputation":480923,"creation_date":1222642783,"user_type":"registered","user_id":23283,"accept_rate":100,"location":"Redmond, WA","website_url":"http://blog.paranoidcoding.com/","link":"http://stackoverflow.com/users/23283/jaredpar","profile_image":"https://www.gravatar.com/avatar/529ba429a58902bef56c2fcb672d5ccb?s=128&d=identicon&r=PG","display_name":"JaredPar"},{"badge_counts":{"bronze":1230,"silver":1159,"gold":97},"account_id":24377,"is_employee":false,"last_modified_date":1490397070,"last_access_date":1493197891,"age":37,"reputation_change_year":20328,"reputation_change_quarter":4478,"reputation_change_month":4478,"reputation_change_week":690,"reputation_change_day":100,"reputation":480723,"creation_date":1233672960,"user_type":"registered","user_id":61974,"location":"Denmark","website_url":"http://careers.stackoverflow.com/markbyers/","link":"http://stackoverflow.com/users/61974/mark-byers","profile_image":"https://www.gravatar.com/avatar/ad240ed5cc406759f0fd72591dc8ca47?s=128&d=identicon&r=PG","display_name":"Mark Byers"},{"badge_counts":{"bronze":996,"silver":856,"gold":74},"account_id":10930,"is_employee":false,"last_modified_date":1492575381,"last_access_date":1493177271,"reputation_change_year":19356,"reputation_change_quarter":4306,"reputation_change_month":4306,"reputation_change_week":700,"reputation_change_day":70,"reputation":477154,"creation_date":1222135580,"user_type":"registered","user_id":20862,"accept_rate":60,"website_url":"","link":"http://stackoverflow.com/users/20862/ignacio-vazquez-abrams","profile_image":"https://www.gravatar.com/avatar/2343ae368d3241278581ce6c87f62a25?s=128&d=identicon&r=PG","display_name":"Ignacio Vazquez-Abrams"},{"badge_counts":{"bronze":768,"silver":458,"gold":59},"account_id":26521,"is_employee":false,"last_modified_date":1492981215,"last_access_date":1492523829,"age":46,"reputation_change_year":17061,"reputation_change_quarter":3469,"reputation_change_month":3469,"reputation_change_week":488,"reputation_change_day":78,"reputation":474186,"creation_date":1235161492,"user_type":"registered","user_id":69083,"location":"V&#228;sterv&#229;la, Sweden","website_url":"http://www.guffa.com","link":"http://stackoverflow.com/users/69083/guffa","profile_image":"https://www.gravatar.com/avatar/1db0cdfd3fe268e270ec481a73046c2f?s=128&d=identicon&r=PG","display_name":"Guffa"},{"badge_counts":{"bronze":870,"silver":547,"gold":67},"account_id":8423,"is_employee":false,"last_modified_date":1492400322,"last_access_date":1493189855,"age":56,"reputation_change_year":19255,"reputation_change_quarter":3950,"reputation_change_month":3950,"reputation_change_week":495,"reputation_change_day":65,"reputation":470767,"creation_date":1221633947,"user_type":"registered","user_id":15168,"accept_rate":100,"location":"California, USA","website_url":"http://None","link":"http://stackoverflow.com/users/15168/jonathan-leffler","profile_image":"https://i.stack.imgur.com/WtEI9.png?s=128&g=1","display_name":"Jonathan Leffler"},{"badge_counts":{"bronze":1041,"silver":1109,"gold":94},"account_id":7598,"is_employee":true,"last_modified_date":1491957818,"last_access_date":1493156333,"age":31,"reputation_change_year":16490,"reputation_change_quarter":3431,"reputation_change_month":3431,"reputation_change_week":615,"reputation_change_day":120,"reputation":458950,"creation_date":1221587590,"user_type":"moderator","user_id":13249,"accept_rate":100,"location":"Winston-Salem, NC","website_url":"https://nickcraver.com/blog/","link":"http://stackoverflow.com/users/13249/nick-craver","profile_image":"https://i.stack.imgur.com/nGCYr.jpg?s=128&g=1","display_name":"Nick Craver"},{"badge_counts":{"bronze":1014,"silver":632,"gold":29},"account_id":237126,"is_employee":false,"last_modified_date":1493119686,"last_access_date":1493203046,"age":46,"reputation_change_year":14964,"reputation_change_quarter":2727,"reputation_change_month":2727,"reputation_change_week":376,"reputation_change_day":30,"reputation":454652,"creation_date":1251226343,"user_type":"registered","user_id":505088,"accept_rate":92,"location":"Ulverston, United Kingdom","website_url":"","link":"http://stackoverflow.com/users/505088/david-heffernan","profile_image":"https://www.gravatar.com/avatar/3c0aac2191718ef0309dbc034d9b9961?s=128&d=identicon&r=PG","display_name":"David Heffernan"},{"badge_counts":{"bronze":1776,"silver":918,"gold":123},"account_id":32093,"is_employee":false,"last_modified_date":1493008193,"last_access_date":1493181589,"age":44,"reputation_change_year":17741,"reputation_change_quarter":3418,"reputation_change_month":3418,"reputation_change_week":544,"reputation_change_day":40,"reputation":446761,"creation_date":1239204526,"user_type":"registered","user_id":88656,"location":"Seattle, WA","website_url":"http://ericlippert.com","link":"http://stackoverflow.com/users/88656/eric-lippert","profile_image":"https://www.gravatar.com/avatar/6fbdff3ffb6f111d172759b4f05bea0e?s=128&d=identicon&r=PG","display_name":"Eric Lippert"},{"badge_counts":{"bronze":797,"silver":625,"gold":33},"account_id":277416,"is_employee":false,"last_modified_date":1492947249,"last_access_date":1493205882,"age":41,"reputation_change_year":20769,"reputation_change_quarter":4005,"reputation_change_month":4005,"reputation_change_week":395,"reputation_change_day":80,"reputation":435628,"creation_date":1294757277,"user_type":"registered","user_id":571407,"accept_rate":100,"location":"Saint-Etienne, France","website_url":"http://jnizet.free.fr","link":"http://stackoverflow.com/users/571407/jb-nizet","profile_image":"https://www.gravatar.com/avatar/2f0d9dec16bae1e06552af55ddefc11f?s=128&d=identicon&r=PG","display_name":"JB Nizet"},{"badge_counts":{"bronze":884,"silver":792,"gold":123},"account_id":9867,"is_employee":false,"last_modified_date":1487806870,"last_access_date":1488317080,"age":44,"reputation_change_year":15810,"reputation_change_quarter":2974,"reputation_change_month":2974,"reputation_change_week":444,"reputation_change_day":80,"reputation":431795,"creation_date":1221783887,"user_type":"registered","user_id":18393,"accept_rate":82,"location":"New York, NY","website_url":"http://www.cforcoding.com","link":"http://stackoverflow.com/users/18393/cletus","profile_image":"https://www.gravatar.com/avatar/2f364c2e36b52bc80296cbf23da8b231?s=128&d=identicon&r=PG","display_name":"cletus"},{"badge_counts":{"bronze":714,"silver":688,"gold":89},"account_id":76141,"is_employee":false,"last_modified_date":1493140750,"last_access_date":1493196059,"age":31,"reputation_change_year":22857,"reputation_change_quarter":4983,"reputation_change_month":4983,"reputation_change_week":815,"reputation_change_day":190,"reputation":430077,"creation_date":1259104089,"user_type":"registered","user_id":218196,"accept_rate":100,"location":"Sunnyvale, CA","website_url":"http://felix-kling.de","link":"http://stackoverflow.com/users/218196/felix-kling","profile_image":"https://i.stack.imgur.com/4P5DY.jpg?s=128&g=1","display_name":"Felix Kling"},{"badge_counts":{"bronze":694,"silver":592,"gold":70},"account_id":21746,"is_employee":false,"last_modified_date":1492267175,"last_access_date":1488292799,"age":32,"reputation_change_year":17530,"reputation_change_quarter":3655,"reputation_change_month":3655,"reputation_change_week":500,"reputation_change_day":110,"reputation":423311,"creation_date":1231452721,"user_type":"registered","user_id":53114,"accept_rate":45,"location":"Ulm, Germany","website_url":"https://www.linkedin.com/in/markuswulftange","link":"http://stackoverflow.com/users/53114/gumbo","profile_image":"https://www.gravatar.com/avatar/cd501083459cbc21fccae78e2d03bee2?s=128&d=identicon&r=PG","display_name":"Gumbo"}]
     * has_more : true
     * quota_max : 300
     * quota_remaining : 297
     */

    private boolean has_more;
    private int quota_max;
    private int quota_remaining;
    private List<ItemsBean> items;

    public boolean isHas_more() {
        return has_more;
    }

    public void setHas_more(boolean has_more) {
        this.has_more = has_more;
    }

    public int getQuota_max() {
        return quota_max;
    }

    public void setQuota_max(int quota_max) {
        this.quota_max = quota_max;
    }

    public int getQuota_remaining() {
        return quota_remaining;
    }

    public void setQuota_remaining(int quota_remaining) {
        this.quota_remaining = quota_remaining;
    }

    public List<ItemsBean> getItems() {
        return items;
    }

    public void setItems(List<ItemsBean> items) {
        this.items = items;
    }

    public static class ItemsBean {
        /**
         * badge_counts : {"bronze":7741,"silver":6906,"gold":545}
         * account_id : 11683
         * is_employee : false
         * last_modified_date : 1493206176
         * last_access_date : 1493205320
         * age : 40
         * reputation_change_year : 25001
         * reputation_change_quarter : 5739
         * reputation_change_month : 5739
         * reputation_change_week : 935
         * reputation_change_day : 260
         * reputation : 942345
         * creation_date : 1222430705
         * user_type : registered
         * user_id : 22656
         * accept_rate : 89
         * location : Reading, United Kingdom
         * website_url : http://csharpindepth.com
         * link : http://stackoverflow.com/users/22656/jon-skeet
         * profile_image : https://www.gravatar.com/avatar/6d8ebb117e8d83d74ea95fbdd0f87e13?s=128&d=identicon&r=PG
         * display_name : Jon Skeet
         */

        private BadgeCountsBean badge_counts;
        private int account_id;
        private boolean is_employee;
        private int last_modified_date;
        private int last_access_date;
        private int age;
        private int reputation_change_year;
        private int reputation_change_quarter;
        private int reputation_change_month;
        private int reputation_change_week;
        private int reputation_change_day;
        private int reputation;
        private int creation_date;
        private String user_type;
        private int user_id;
        private int accept_rate;
        private String location;
        private String website_url;
        private String link;
        private String profile_image;
        private String display_name;

        public BadgeCountsBean getBadge_counts() {
            return badge_counts;
        }

        public void setBadge_counts(BadgeCountsBean badge_counts) {
            this.badge_counts = badge_counts;
        }

        public int getAccount_id() {
            return account_id;
        }

        public void setAccount_id(int account_id) {
            this.account_id = account_id;
        }

        public boolean isIs_employee() {
            return is_employee;
        }

        public void setIs_employee(boolean is_employee) {
            this.is_employee = is_employee;
        }

        public int getLast_modified_date() {
            return last_modified_date;
        }

        public void setLast_modified_date(int last_modified_date) {
            this.last_modified_date = last_modified_date;
        }

        public int getLast_access_date() {
            return last_access_date;
        }

        public void setLast_access_date(int last_access_date) {
            this.last_access_date = last_access_date;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public int getReputation_change_year() {
            return reputation_change_year;
        }

        public void setReputation_change_year(int reputation_change_year) {
            this.reputation_change_year = reputation_change_year;
        }

        public int getReputation_change_quarter() {
            return reputation_change_quarter;
        }

        public void setReputation_change_quarter(int reputation_change_quarter) {
            this.reputation_change_quarter = reputation_change_quarter;
        }

        public int getReputation_change_month() {
            return reputation_change_month;
        }

        public void setReputation_change_month(int reputation_change_month) {
            this.reputation_change_month = reputation_change_month;
        }

        public int getReputation_change_week() {
            return reputation_change_week;
        }

        public void setReputation_change_week(int reputation_change_week) {
            this.reputation_change_week = reputation_change_week;
        }

        public int getReputation_change_day() {
            return reputation_change_day;
        }

        public void setReputation_change_day(int reputation_change_day) {
            this.reputation_change_day = reputation_change_day;
        }

        public int getReputation() {
            return reputation;
        }

        public void setReputation(int reputation) {
            this.reputation = reputation;
        }

        public int getCreation_date() {
            return creation_date;
        }

        public void setCreation_date(int creation_date) {
            this.creation_date = creation_date;
        }

        public String getUser_type() {
            return user_type;
        }

        public void setUser_type(String user_type) {
            this.user_type = user_type;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public int getAccept_rate() {
            return accept_rate;
        }

        public void setAccept_rate(int accept_rate) {
            this.accept_rate = accept_rate;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getWebsite_url() {
            return website_url;
        }

        public void setWebsite_url(String website_url) {
            this.website_url = website_url;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getProfile_image() {
            return profile_image;
        }

        public void setProfile_image(String profile_image) {
            this.profile_image = profile_image;
        }

        public String getDisplay_name() {
            return display_name;
        }

        public void setDisplay_name(String display_name) {
            this.display_name = display_name;
        }

        public static class BadgeCountsBean {
            /**
             * bronze : 7741
             * silver : 6906
             * gold : 545
             */

            private int bronze;
            private int silver;
            private int gold;

            public int getBronze() {
                return bronze;
            }

            public void setBronze(int bronze) {
                this.bronze = bronze;
            }

            public int getSilver() {
                return silver;
            }

            public void setSilver(int silver) {
                this.silver = silver;
            }

            public int getGold() {
                return gold;
            }

            public void setGold(int gold) {
                this.gold = gold;
            }
        }
    }
}

package net.thetabx.gksa.libGKSj;

import net.thetabx.gksa.libGKSj.http.AsyncFetcher;
import net.thetabx.gksa.libGKSj.http.AsyncListener;
import net.thetabx.gksa.libGKSj.http.HttpWrapper;
import net.thetabx.gksa.libGKSj.objects.Credentials;
import net.thetabx.gksa.libGKSj.objects.Forum;
import net.thetabx.gksa.libGKSj.objects.Forums;
import net.thetabx.gksa.libGKSj.objects.GObject;
import net.thetabx.gksa.libGKSj.objects.GStatus;
import net.thetabx.gksa.libGKSj.objects.UserMe;
import net.thetabx.gksa.libGKSj.objects.UserProfile;

import java.io.IOException;

/**
 * Created by Zerg on 18/06/13.
 */
public class GKS {
    public String meUserId;
    private HttpWrapper http;

    /*
    Basic auth interaction

    If(neverConnected)
        Show(credsActivity)
            Connect.onClick()
                this.connect()
                if(connected)
                    storeToken()
                    fetchUser(meId)
                    onResult(me)
                    finishActivity
                else
                    Toast(wrongCreds)
        credsActivity.onFinish() ?
            if(GKS.isConnected)
                updateWelcomeActivity(me)
            else
                show(credsActivity)

    else if(tokenStored)
        fetchUser(meId)
        if(fetchedIsOk)
            updateWelcomeActivity(me)
        else
            this.connect()
            if(connected)
                storeToken()
                fetchUser(meId)
                updateWelcomeActivity(me)
            else
                show(credsActivity)
    */

    public GKS() {
        http = new HttpWrapper();
    }

    public void setUserToken(String userId, String token) {
        this.meUserId = userId;
        http.setUserToken(userId, token);
    }

    public void connect(final String user, final String password, final AsyncListener progressListener) {
        // Check for 302
        class Fetcher extends AsyncFetcher {
            @Override
            protected GStatus doInBackground(String... strings) {
                if(http.ForceConnection(strings[0], strings[1])) {
                    this.parsedObject = new Credentials(http.getUserId(), http.getToken());
                    return GStatus.OK;
                }
                return GStatus.BADCREDS;
            }
        }
        final AsyncListener proxyListener = new AsyncListener() {
            @Override
            public void onPreExecute() {
                progressListener.onPreExecute();
            }

            @Override
            public void onPostExecute(GStatus status, GObject result) {
                if(status == GStatus.OK)
                    meUserId = ((Credentials)result).getUserId();
                progressListener.onPostExecute(status, result);
            }
        };
        new Fetcher().SetParams(http, proxyListener).execute(user, password);
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    public void fetchUserMe(AsyncListener progressListener) throws IOException {
        fetchUser(meUserId, progressListener);
    }

    // If no cached Me or Me(id) != uid, fetch user
    public void fetchUser(final String userId, AsyncListener progressListener) throws IOException {
        class Fetcher extends AsyncFetcher {
            @Override
            protected GStatus doInBackground(String... urlFragments) {
                try {
                    String html = http.getUrl(String.format(urlFragments[0], urlFragments[1]));
                    if(html.isEmpty())
                        return GStatus.NOHTTP;
                    if(userId.equals(meUserId))
                        parsedObject = new UserMe(html, urlFragments);
                    else
                        parsedObject = new UserProfile(html, urlFragments);
                } catch (IOException e) {
                    return GStatus.ERROR;
                }
                return GStatus.OK;
            }
        }
        new Fetcher().SetParams(http, progressListener).execute("/users/%s", userId);
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    public void fetchMPList(AsyncListener progressListener) {
        class Fetcher extends AsyncFetcher {
            @Override
            protected GStatus doInBackground(String... urlFragments) {
                /*try {
                    parsedObject = new MPList(http.getUrl(urlFragments[0]), urlFragments);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                return GStatus.OK;
            }
        }
        new Fetcher().SetParams(http, progressListener).execute("/mailbox/");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void fetchMP(int conversationId, AsyncListener progressListener) {
        class Fetcher extends AsyncFetcher {
            @Override
            protected GStatus doInBackground(String... urlFragments) {
                /*try {
                    parsedObject = new MPConversation(http.getUrl(String.format(urlFragments[0], urlFragments[1])), urlFragments);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                return GStatus.OK;
            }
        }
        new Fetcher().SetParams(http, progressListener).execute("/mailbox/?mb&conversation=%s", Integer.toString(conversationId));
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void fetchTwitsList(AsyncListener progressListener) {
        class Fetcher extends AsyncFetcher {
            @Override
            protected GStatus doInBackground(String... urlFragments) {
                /*try {
                    parsedObject = new TwitsList(http.getUrl(urlFragments[0]), urlFragments);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                return GStatus.OK;
            }
        }
        new Fetcher().SetParams(http, progressListener).execute("/m/aura/");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void fetchForums(AsyncListener progressListener) {
        class Fetcher extends AsyncFetcher {
            @Override
            protected GStatus doInBackground(String... urlFragments) {
                try {
                    parsedObject = new Forums(http.getUrl(urlFragments[0]), urlFragments);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return GStatus.OK;
            }
        }
        new Fetcher().SetParams(http, progressListener).execute("/forums.php");
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    public void fetchForum(int forumId, AsyncListener progressListener) {
        fetchForum(forumId, 1, progressListener);
    }

    public void fetchForum(int forumId, int page, AsyncListener progressListener) {
        class Fetcher extends AsyncFetcher {
            @Override
            protected GStatus doInBackground(String... urlFragments) {
                try {
                    parsedObject = new Forum(http.getUrl(String.format(urlFragments[0], urlFragments[1], urlFragments[2])), urlFragments);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return GStatus.OK;
            }
        }
        new Fetcher().SetParams(http, progressListener).execute("/forums.php?action=viewforum&forumid=%s&page=%s", Integer.toString(forumId), Integer.toString(page));
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    public void fetchTopic(int topicId, AsyncListener progressListener) {
        fetchTopic(topicId, 1, progressListener);
    }

    public void fetchTopic(int topicId, int page, AsyncListener progressListener) {
        class Fetcher extends AsyncFetcher {
            @Override
            protected GStatus doInBackground(String... urlFragments) {
                /*try {
                    parsedObject = new Topic(http.getUrl(String.format(urlFragments[0], urlFragments[1], urlFragments[2])), urlFragments);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                return GStatus.OK;
            }
        }
        new Fetcher().SetParams(http, progressListener).execute("/forums.php?action=viewtopic&topicid=%s&page=%s", Integer.toString(topicId), Integer.toString(page));
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void fetchBookmarks(AsyncListener progressListener) {
        class Fetcher extends AsyncFetcher {
            @Override
            protected GStatus doInBackground(String... urlFragments) {
                /*try {
                    parsedObject = new BookmarksList(http.getUrl(urlFragments[0]), urlFragments);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                return GStatus.OK;
            }
        }
        new Fetcher().SetParams(http, progressListener).execute("/bookmark/");
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

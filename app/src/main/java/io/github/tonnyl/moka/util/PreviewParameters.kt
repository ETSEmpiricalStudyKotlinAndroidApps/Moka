package io.github.tonnyl.moka.util

import android.net.Uri
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.github.tonnyl.moka.data.*
import io.github.tonnyl.moka.data.item.*
import io.github.tonnyl.moka.data.item.User
import io.github.tonnyl.moka.type.CommentAuthorAssociation
import io.github.tonnyl.moka.type.CommentCannotUpdateReason
import io.github.tonnyl.moka.type.PullRequestState
import io.github.tonnyl.moka.type.ReactionContent
import kotlinx.datetime.Instant

class TimelineEventProvider : PreviewParameterProvider<Event> {

    override val values: Sequence<Event>
        get() = sequenceOf(
            Event(
                id = "14920513551",
                type = "WatchEvent",
                isPublic = true,
                actor = EventActor(
                    id = 8949716,
                    login = "bingoogolapple",
                    htmlUrl = "https://api.github.com/users/bingoogolapple",
                    avatarUrl = "https://avatars.githubusercontent.com/u/8949716?",
                    type = "User"
                ),
                repo = EventRepository(
                    id = "168500397",
                    name = "justauth/JustAuth",
                    fullName = null,
                    url = "https://api.github.com/repos/justauth/JustAuth",
                    htmlUrl = null
                ),
                org = EventOrg(
                    id = 53901302,
                    login = "justauth",
                    grAvatarId = "",
                    url = "https://api.github.com/orgs/justauth",
                    avatarUrl = "https://avatars.githubusercontent.com/u/53901302?"
                ),
                createdAt = Instant.parse("2021-01-23T05:11:13Z"),
                payload = EventPayload(
                    action = "started",
                    comment = null,
                    commitComment = null,
                    issue = null,
                    pullRequest = null,
                    review = null,
                    download = null,
                    target = null,
                    forkee = null,
                    gist = null,
                    pages = null,
                    member = null,
                    team = null,
                    organization = null,
                    release = null,
                    blockedUser = null,
                    projectCard = null,
                    projectColumn = null,
                    membership = null,
                    invitation = null,
                    project = null,
                    size = null,
                    refType = null,
                    ref = null
                ),
                ref = null,
                refType = null,
                masterBranch = null,
                description = null,
                pusherType = null,
                head = null,
                before = null
            ),
            Event(
                id = "14911487663",
                type = "MemberEvent",
                isPublic = true,
                actor = EventActor(
                    id = 227486,
                    login = "chrisbanes",
                    htmlUrl = "https://api.github.com/users/chrisbanes",
                    avatarUrl = "https://avatars.githubusercontent.com/u/227486?",
                    type = "User"
                ),
                repo = EventRepository(
                    id = "261392630",
                    name = "chrisbanes/accompanist",
                    url = "https://api.github.com/repos/chrisbanes/accompanist",
                    fullName = null,
                    htmlUrl = null
                ),
                org = null,
                createdAt = Instant.parse("2021-01-22T11:08:57Z"),
                payload = EventPayload(
                    action = "added",
                    comment = null,
                    commitComment = null,
                    issue = null,
                    pullRequest = null,
                    review = null,
                    download = null,
                    target = null,
                    forkee = null,
                    gist = null,
                    pages = null,
                    member = EventActor(
                        login = "florina-muntenescu",
                        id = 2998890,
                        avatarUrl = "https://avatars.githubusercontent.com/u/2998890?v=4",
                        htmlUrl = "https://github.com/florina-muntenescu",
                        type = "User"
                    ),
                    team = null,
                    organization = null,
                    release = null,
                    blockedUser = null,
                    projectCard = null,
                    projectColumn = null,
                    membership = null,
                    invitation = null,
                    project = null,
                    size = null,
                    refType = null,
                    ref = null
                ),
                ref = null,
                refType = null,
                masterBranch = null,
                description = null,
                pusherType = null,
                head = null,
                before = null
            )
        )

}

class NotificationProvider : PreviewParameterProvider<Notification> {

    override val values: Sequence<Notification>
        get() = sequenceOf(
            Notification(
                id = "1377378118",
                repository = NotificationRepository(
                    id = 3432266,
                    nodeId = "MDEwOlJlcG9zaXRvcnkzNDMyMjY2",
                    name = "kotlin",
                    fullName = "JetBrains/kotlin",
                    isPrivate = false,
                    owner = NotificationRepositoryOwner(
                        login = "JetBrains",
                        id = 878437,
                        nodeId = "MDEyOk9yZ2FuaXphdGlvbjg3ODQzNw==",
                        avatarUrl = "https://avatars.githubusercontent.com/u/878437?v=4",
                        gravatarId = "",
                        url = "https://api.github.com/users/JetBrains",
                        htmlUrl = "https://github.com/JetBrains",
                        followersUrl = "https://api.github.com/users/JetBrains/followers",
                        followingUrl = "https://api.github.com/users/JetBrains/following{/other_user}",
                        gistsUrl = "https://api.github.com/users/JetBrains/gists{/gist_id}",
                        starredUrl = "https://api.github.com/users/JetBrains/starred{/owner}{/repo}",
                        subscriptionsUrl = "https://api.github.com/users/JetBrains/subscriptions",
                        organizationsUrl = "https://api.github.com/users/JetBrains/orgs",
                        reposUrl = "https://api.github.com/users/JetBrains/repos",
                        eventsUrl = "https://api.github.com/users/JetBrains/events{/privacy}",
                        receivedEventsUrl = "https://api.github.com/users/JetBrains/received_events",
                        type = "Organization",
                        siteAdmin = false
                    ),
                    htmlUrl = "https://github.com/JetBrains/kotlin",
                    description = "The Kotlin Programming Language",
                    fork = false,
                    url = "https://api.github.com/repos/JetBrains/kotlin",
                ),
                reason = NotificationReasons.SUBSCRIBED,
                subject = NotificationRepositorySubject(
                    title = "Kotlin 1.4.21",
                    url = "https://api.github.com/repos/JetBrains/kotlin/releases/34918478",
                    latestCommentUrl = "https://api.github.com/repos/JetBrains/kotlin/releases/34918478",
                    type = "Release"
                ),
                unread = false,
                updatedAt = Instant.parse("2020-12-09T15:21:46Z"),
                lastReadAt = Instant.parse("2020-12-08T03:10:28Z"),
                url = "https://api.github.com/notifications/threads/1377378118",
                hasDisplayed = true
            ),
            Notification(
                id = "1358008902",
                repository = NotificationRepository(
                    id = 112747419,
                    nodeId = "MDEwOlJlcG9zaXRvcnkxMTI3NDc0MTk=",
                    name = "WhatsNew",
                    fullName = "TonnyL/WhatsNew",
                    isPrivate = false,
                    owner = NotificationRepositoryOwner(
                        login = "TonnyL",
                        id = 13329148,
                        nodeId = "MDQ6VXNlcjEzMzI5MTQ4",
                        avatarUrl = "https://avatars.githubusercontent.com/u/13329148?v=4",
                        gravatarId = "",
                        url = "https://api.github.com/users/TonnyL",
                        htmlUrl = "https://github.com/TonnyL",
                        followersUrl = "https://api.github.com/users/TonnyL/followers",
                        followingUrl = "https://api.github.com/users/TonnyL/following{/other_user}",
                        gistsUrl = "https://api.github.com/users/TonnyL/gists{/gist_id}",
                        starredUrl = "https://api.github.com/users/TonnyL/starred{/owner}{/repo}",
                        subscriptionsUrl = "https://api.github.com/users/TonnyL/subscriptions",
                        organizationsUrl = "https://api.github.com/users/TonnyL/orgs",
                        reposUrl = "https://api.github.com/users/TonnyL/repos",
                        eventsUrl = "https://api.github.com/users/TonnyL/events{/privacy}",
                        receivedEventsUrl = "https://api.github.com/users/TonnyL/received_events",
                        type = "User",
                        siteAdmin = false
                    ),
                    htmlUrl = "https://github.com/TonnyL/WhatsNew",
                    description = "🎉 WhatsNew automatically displays a short description of the new features when users update your app",
                    fork = false,
                    url = "https://api.github.com/repos/TonnyL/WhatsNew"
                ),
                subject = NotificationRepositorySubject(
                    title = "Only notify when upgrading application, not on first usage",
                    url = "https://api.github.com/repos/TonnyL/WhatsNew/issues/21",
                    latestCommentUrl = "https://api.github.com/repos/TonnyL/WhatsNew/issues/21",
                    type = "Issue"
                ),
                reason = NotificationReasons.SUBSCRIBED,
                unread = false,
                updatedAt = Instant.parse("2020-11-30T13:09:17Z"),
                lastReadAt = Instant.parse("2020-11-30T14:42:09Z"),
                url = "https://api.github.com/notifications/threads/1358008902",
                hasDisplayed = false
            )
        )

}

class TrendingDeveloperProvider : PreviewParameterProvider<TrendingDeveloper> {

    override val values: Sequence<TrendingDeveloper>
        get() = sequenceOf(
            TrendingDeveloper(
                id = 0,
                username = "google",
                name = "Google",
                type = "organization",
                url = "https://github.com/google",
                avatar = "https://avatars0.githubusercontent.com/u/1342004",
                repository = TrendingDeveloperRepository(
                    name = "traceur-compiler",
                    description = "Traceur is a JavaScript.next-to-JavaScript-of-today compiler",
                    url = "https://github.com/google/traceur-compiler"
                )
            )
        )

}

class TrendingRepositoryProvider : PreviewParameterProvider<TrendingRepository> {

    override val values: Sequence<TrendingRepository>
        get() = sequenceOf(
            TrendingRepository(
                id = 0,
                author = "google",
                name = "gvisor",
                avatar = "https://github.com/google.png",
                url = "https://github.com/google/gvisor",
                description = "Container Runtime Sandbox",
                language = "Go",
                languageColor = "#3572A5",
                stars = 3320,
                forks = 118,
                currentPeriodStars = 1624,
                builtBy = listOf(
                    TrendingRepositoryBuiltBy(
                        href = "https://github.com/viatsko",
                        avatar = "https://avatars0.githubusercontent.com/u/376065",
                        username = "viatsko"
                    )
                )
            )
        )

}

class IssueItemProvider : PreviewParameterProvider<IssueItem> {

    override val values: Sequence<IssueItem>
        get() = sequenceOf(
            IssueItem(
                actor = Actor(
                    avatarUrl = Uri.parse("https://avatars.githubusercontent.com/u/1776230?u=b99f29305efeed615ccbf10236f01a6a0bee739e&v=4"),
                    login = "eneim",
                    url = Uri.parse("https://github.com/eneim")
                ),
                id = "MDU6SXNzdWU1MjEyNzQ1MjU=",
                number = 1,
                createdAt = Instant.parse("2019-11-12T01:24:24Z"),
                title = "[Feature request] Adding version distribution",
                closed = false
            ),
            IssueItem(
                actor = Actor(
                    avatarUrl = Uri.parse("https://avatars.githubusercontent.com/u/11989916?u=496f5fa6421a661441ef6467a909be31cb7be9c6&v=4"),
                    login = "caiyoufei",
                    url = Uri.parse("https://github.com/caiyoufei")
                ),
                id = "MDU6SXNzdWU1MjQ5NTIyNTg=",
                number = 4,
                createdAt = Instant.parse("2019-11-19T11:33:48Z"),
                title = "A error version",
                closed = true
            )
        )

}

class PullRequestItemProvider : PreviewParameterProvider<PullRequestItem> {

    override val values: Sequence<PullRequestItem>
        get() = sequenceOf(
            PullRequestItem(
                actor = Actor(
                    avatarUrl = Uri.parse("https://avatars.githubusercontent.com/u/5749794?u=23b1d3479940253cd4848bf7c5b4520019a4c372&v=4"),
                    login = "jshvarts",
                    url = Uri.parse("https://github.com/jshvarts")
                ),
                closed = true,
                createdAt = Instant.parse("2019-11-15T03:41:07Z"),
                id = "MDExOlB1bGxSZXF1ZXN0MzQxMjc5NzE5",
                merged = true,
                number = 2,
                title = "Fix grammar"
            ),
            PullRequestItem(
                actor = Actor(
                    avatarUrl = Uri.parse("https://avatars.githubusercontent.com/u/13329148?u=5f2267ec07a7e93d6281173e865faeb2363ff658&v=4"),
                    login = "TonnyL",
                    url = Uri.parse("https://github.com/TonnyL")
                ),
                closed = true,
                createdAt = Instant.parse("2020-03-12T14:49:22Z"),
                id = "MDExOlB1bGxSZXF1ZXN0Mzg3Mjg4MjUz",
                merged = true,
                number = 5,
                title = "handle network request / json-parsing errors"
            ),
            PullRequestItem(
                actor = Actor(
                    avatarUrl = Uri.parse("https://avatars.githubusercontent.com/u/13329148?u=5f2267ec07a7e93d6281173e865faeb2363ff658&v=4"),
                    login = "TonnyL",
                    url = Uri.parse("https://github.com/TonnyL")
                ),
                closed = true,
                createdAt = Instant.parse("2020-03-15T09:13:55Z"),
                id = "MDExOlB1bGxSZXF1ZXN0Mzg4NjM2NTMw",
                merged = false,
                number = 6,
                title = "add distribution data"
            )
        )

}

private val assignedEvent = AssignedEvent(
    actor = Actor(
        avatarUrl = Uri.parse("https://avatars.githubusercontent.com/u/352556?u=4c1ac7af0e0164029f36f8ecf262f3920d2e7b4f&v=4"),
        login = "nickbutcher",
        url = Uri.parse("https://github.com/nickbutcher")
    ),
    createdAt = Instant.parse("2020-09-21T12:07:57Z"),
    id = "MDQ6VXNlcjM1MjU1Ng==",
    assigneeLogin = "nickbutcher",
    assigneeName = "Nick Butcher"
)

class IssueTimelineEventProvider : PreviewParameterProvider<IssueTimelineItem> {

    override val values: Sequence<IssueTimelineItem>
        get() = sequenceOf(assignedEvent)

}

private val commentEvent = IssueComment(
    author = Actor(
        avatarUrl = Uri.parse("https://avatars.githubusercontent.com/u/352556?u=4c1ac7af0e0164029f36f8ecf262f3920d2e7b4f&v=4"),
        login = "nickbutcher",
        url = Uri.parse("https://github.com/nickbutcher")
    ),
    authorAssociation = CommentAuthorAssociation.COLLABORATOR,
    createdAt = Instant.parse("2020-09-21T12:07:53Z"),
    displayHtml = "Thanks for reporting. I'm planning to migrate this screen to use the new [`BottomSheetScaffold`](https://cs.android.com/androidx/platform/frameworks/support/+/androidx-master-dev:compose/material/material/src/commonMain/kotlin/androidx/compose/material/BottomSheetScaffold.kt;l=267) which should hopefully also address this.",
    id = "MDEyOklzc3VlQ29tbWVudDY5NjA3MjE3Nw==",
    editor = null,
    reactionGroups = mutableListOf(
        ReactionGroup(
            content = ReactionContent.THUMBS_UP,
            viewerHasReacted = true,
            usersTotalCount = 1
        ),
        ReactionGroup(
            content = ReactionContent.THUMBS_DOWN,
            viewerHasReacted = false,
            usersTotalCount = 1
        )
    ),
    viewerCanDelete = false,
    viewerCanReact = true,
    viewerDidAuthor = false,
    viewerCanUpdate = false,
    viewerCanMinimize = false,
    viewerCannotUpdateReasons = listOf(
        CommentCannotUpdateReason.INSUFFICIENT_ACCESS
    )
)

class IssueTimelineCommentProvider : PreviewParameterProvider<IssueTimelineItem> {

    override val values: Sequence<IssueTimelineItem>
        get() = sequenceOf(commentEvent)

}

class IssueTimelineItemProvider : PreviewParameterProvider<IssueTimelineItem> {

    override val values: Sequence<IssueTimelineItem>
        get() = sequenceOf(
            commentEvent,
            assignedEvent
        )

}

class PullRequestTimelineItemProvider : PreviewParameterProvider<PullRequestTimelineItem> {

    override val values: Sequence<PullRequestTimelineItem>
        get() = sequenceOf(
            PullRequestCommit(
                commit = PullRequestTimelineItemCommit(
                    author = PullRequestTimelineItemGitActor(
                        avatarUrl = Uri.parse("https://avatars.githubusercontent.com/u/352556?v=4"),
                        email = "nickbutcher@google.com",
                        name = "Nick Butcher",
                        user = User(
                            avatarUrl = Uri.parse("https://avatars.githubusercontent.com/u/352556?u=4c1ac7af0e0164029f36f8ecf262f3920d2e7b4f&v=4"),
                            login = "nickbutcher",
                            name = "Nick Butcher",
                            url = Uri.parse("https://github.com/nickbutcher"),
                            id = "MDQ6VXNlcjM1MjU1Ng=="
                        )
                    ),
                    committer = PullRequestTimelineItemGitActor(
                        avatarUrl = Uri.parse("https://avatars.githubusercontent.com/u/352556?v=4"),
                        email = "nickbutcher@google.com",
                        name = "Nick Butcher",
                        user = User(
                            avatarUrl = Uri.parse("https://avatars.githubusercontent.com/u/352556?u=4c1ac7af0e0164029f36f8ecf262f3920d2e7b4f&v=4"),
                            login = "nickbutcher",
                            name = "Nick Butcher",
                            url = Uri.parse("https://github.com/nickbutcher"),
                            id = "MDQ6VXNlcjM1MjU1Ng=="
                        )
                    ),
                    message = "Update to snapshot 6994167.\n\nChange-Id: Ia634dfda0d83b5aaa187d6bb4a50cfb545a78841",
                    oid = "61a4cefac40aa542942dad4ae2e2962aef8e6ba0",
                    url = Uri.parse(
                        "https://github.com/android/compose-samples/commit/61a4cefac40aa542942dad4ae2e2962aef8e6ba0)"
                    )
                ),
                id = "MDE3OlB1bGxSZXF1ZXN0Q29tbWl0NTI3NTI2MTA5OjYxYTRjZWZhYzQwYWE1NDI5NDJkYWQ0YWUyZTI5NjJhZWY4ZTZiYTA=",
                pullRequest = PullRequestTimelineItemPullRequest(
                    closed = false,
                    number = 293,
                    id = "MDExOlB1bGxSZXF1ZXN0NTI3NTI2MTA5",
                    state = PullRequestState.OPEN,
                    title = "[Jetsnack] Update SysUi controller to provide easier theming of status/nav bars",
                    url = Uri.parse("https://github.com/android/compose-samples/pull/293")
                ),
                url = Uri.parse("https://github.com/android/compose-samples/pull/293/commits/61a4cefac40aa542942dad4ae2e2962aef8e6ba0")
            ),
            IssueComment(
                author = Actor(
                    avatarUrl = Uri.parse("https://avatars.githubusercontent.com/u/227486?v=4"),
                    login = "chrisbanes",
                    url = Uri.parse("https://github.com/chrisbanes")
                ),
                authorAssociation = CommentAuthorAssociation.COLLABORATOR,
                createdAt = Instant.parse("2020-11-25T16:16:47Z"),
                displayHtml = "<p>A few things but overall LGTM</p>",
                id = "MDE3OlB1bGxSZXF1ZXN0UmV2aWV3NTM4NjU3ODQ5",
                editor = null,
                reactionGroups = null,
                viewerCanDelete = false,
                viewerCanReact = true,
                viewerCanUpdate = false,
                viewerDidAuthor = false,
                viewerCanMinimize = false,
                viewerCannotUpdateReasons = listOf(
                    CommentCannotUpdateReason.INSUFFICIENT_ACCESS
                )
            )
        )

}

class RepositoryItemProvider : PreviewParameterProvider<RepositoryItem> {

    override val values: Sequence<RepositoryItem>
        get() = sequenceOf(
            RepositoryItem(
                description = "🏀 An Android app for dribbble.com",
                descriptionHTML = "<div>\n<g-emoji class=\"g-emoji\" alias=\"basketball\" fallback-src=\"https://github.githubassets.com/images/icons/emoji/unicode/1f3c0.png\">🏀</g-emoji> An Android app for dribbble.com</div>",
                homepageUrl = Uri.parse(""),
                id = "MDEwOlJlcG9zaXRvcnk5NjA2NzIxMQ==",
                isArchived = false,
                isFork = false,
                isLocked = false,
                isMirror = false,
                isPrivate = false,
                mirrorUrl = null,
                name = "Mango",
                nameWithOwner = "TonnyL/Mango",
                owner = RepositoryOwner(
                    avatarUrl = Uri.parse("https://avatars.githubusercontent.com/u/13329148?u=5f2267ec07a7e93d6281173e865faeb2363ff658&v=4"),
                    id = "MDQ6VXNlcjEzMzI5MTQ4",
                    login = "TonnyL",
                    resourcePath = Uri.parse("/TonnyL"),
                    url = Uri.parse("https://github.com/TonnyL")
                ),
                parent = null,
                primaryLanguage = Language(
                    color = "#F18E33",
                    id = "MDg6TGFuZ3VhZ2UyNzI=",
                    name = "Kotlin"
                ),
                shortDescriptionHTML = "<g-emoji class=\"g-emoji\" alias=\"basketball\" fallback-src=\"https://github.githubassets.com/images/icons/emoji/unicode/1f3c0.png\">🏀</g-emoji> An Android app for dribbble.com",
                url = Uri.parse("https://github.com/TonnyL/Mango"),
                viewerHasStarred = false,
                forksCount = 98,
                stargazersCount = 659
            ),
            RepositoryItem(
                description = "A command-line tool for browsing GitHub trending written by Rust.",
                descriptionHTML = "<div>A command-line tool for browsing GitHub trending written by Rust.</div>",
                homepageUrl = Uri.parse(""),
                id = "MDEwOlJlcG9zaXRvcnkyMjIxMjU4MDk=",
                isArchived = false,
                isFork = false,
                isLocked = false,
                isMirror = false,
                isPrivate = false,
                mirrorUrl = null,
                name = "wukong",
                nameWithOwner = "TonnyL/wukong",
                owner = RepositoryOwner(
                    avatarUrl = Uri.parse("https://avatars.githubusercontent.com/u/13329148?u=5f2267ec07a7e93d6281173e865faeb2363ff658&v=4"),
                    id = "MDQ6VXNlcjEzMzI5MTQ4",
                    login = "TonnyL",
                    resourcePath = Uri.parse("/TonnyL"),
                    url = Uri.parse("https://github.com/TonnyL")
                ),
                parent = null,
                primaryLanguage = Language(
                    color = "#dea584",
                    id = "MDg6TGFuZ3VhZ2UyNDk=",
                    name = "Rust"
                ),
                shortDescriptionHTML = "A command-line tool for browsing GitHub trending written by Rust.",
                url = Uri.parse("https://github.com/TonnyL/wukong"),
                viewerHasStarred = false,
                forksCount = 3,
                stargazersCount = 23
            )
        )

}

class UserItemProvider : PreviewParameterProvider<UserItem> {

    override val values: Sequence<UserItem>
        get() = sequenceOf(
            UserItem(
                avatarUrl = Uri.parse("https://avatars.githubusercontent.com/u/3777021?u=8c2f8d46ac442b51f925e353bef7d80005a249e9&v=4"),
                bio = "Passionate about Android development and product design. Ex Android Infrastructure Tech Lead @ Zhihu",
                bioHTML = "<div>Passionate about Android development and product design. Ex Android Infrastructure Tech Lead @ Zhihu</div>",
                id = "MDQ6VXNlcjM3NzcwMjE=",
                isViewer = false,
                login = "gejiaheng",
                name = "Jiaheng",
                url = Uri.parse("https://github.com/gejiaheng"),
                viewerCanFollow = true,
                viewerIsFollowing = true
            ),
            UserItem(
                avatarUrl = Uri.parse("https://avatars.githubusercontent.com/u/352556?u=4c1ac7af0e0164029f36f8ecf262f3920d2e7b4f&v=4"),
                bio = "Android designer and developer.",
                bioHTML = "<div>Android designer and developer.</div>",
                id = "MDQ6VXNlcjM1MjU1Ng==",
                isViewer = false,
                login = "nickbutcher",
                name = "Nick Butcher",
                url = Uri.parse("https://github.com/nickbutcher"),
                viewerCanFollow = true,
                viewerIsFollowing = true
            )
        )

}

class SearchedOrganizationItemProvider : PreviewParameterProvider<SearchedOrganizationItem> {

    override val values: Sequence<SearchedOrganizationItem>
        get() = sequenceOf(
            SearchedOrganizationItem(
                avatarUrl = Uri.parse("https://avatars.githubusercontent.com/u/32689599?v=4"),
                description = "",
                descriptionHTML = "<div></div>",
                id = "MDEyOk9yZ2FuaXphdGlvbjMyNjg5NTk5",
                isVerified = false,
                login = "android",
                name = "Android",
                url = Uri.parse("https://github.com/android"),
                viewerIsAMember = false,
                websiteUrl = Uri.parse("https://d.android.com")
            ),
            SearchedOrganizationItem(
                avatarUrl = Uri.parse("https://avatars.githubusercontent.com/u/9919?v=4"),
                description = "How people build software.",
                descriptionHTML = "<div>How people build software.</div>",
                id = "MDEyOk9yZ2FuaXphdGlvbjk5MTk=",
                isVerified = true,
                login = "github",
                name = "GitHub",
                url = Uri.parse("https://github.com/github"),
                viewerIsAMember = false,
                websiteUrl = Uri.parse("https://github.com/about")
            )
        )

}

class SearchedEmojiItemProvider : PreviewParameterProvider<SearchableEmoji> {

    override val values: Sequence<SearchableEmoji>
        get() = sequenceOf(
            SearchableEmoji(
                emoji = "😃",
                name = ":smiley:",
                category = "Smileys & Emotion"
            ),
            SearchableEmoji(
                emoji = "😀",
                name = ":grinning:",
                category = "Smileys & Emotion"
            )
        )

}

class EmojiItemProvider : PreviewParameterProvider<Emoji> {

    override val values: Sequence<Emoji>
        get() = sequenceOf(
            Emoji(
                category = "Smileys & Emotion",
                description = "grinning face with big eyes",
                emoji = "😃",
                names = listOf(":smiley:"),
                tags = listOf(
                    "happy",
                    "joy",
                    "haha"
                )
            ),
            Emoji(
                category = "Smileys & Emotion",
                description = "grinning face",
                emoji = "😀",
                names = listOf(
                    ":grinning:"
                ),
                tags = listOf(
                    "smile",
                    "happy"
                )
            ),
            Emoji(
                category = "People & Body",
                description = "waving hand",
                emoji = "👋",
                names = listOf(":wave:"),
                tags = listOf(
                    "goodbye"
                )
            )
        )

}

class EmojiCategoryProvider : PreviewParameterProvider<EmojiCategory> {

    override val values: Sequence<EmojiCategory>
        get() = sequenceOf(
            EmojiCategory.SmileysAndEmotion,
            EmojiCategory.PeopleAndBody
        )

}
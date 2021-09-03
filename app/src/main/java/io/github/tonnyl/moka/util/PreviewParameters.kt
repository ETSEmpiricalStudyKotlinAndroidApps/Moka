package io.github.tonnyl.moka.util

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.github.tonnyl.moka.data.*
import io.github.tonnyl.moka.data.item.*
import io.github.tonnyl.moka.data.item.User
import io.github.tonnyl.moka.fragment.ReleaseListItem
import io.github.tonnyl.moka.fragment.TreeEntry
import io.github.tonnyl.moka.queries.CurrentLevelTreeViewQuery.Data.Repository.TreeObject.Entry
import io.github.tonnyl.moka.queries.UsersRepositoryDefaultCommitsQuery.Data.User.Repository.DefaultBranchRef.CommitTarget.History.Node
import io.github.tonnyl.moka.type.*
import kotlinx.datetime.Instant
import io.github.tonnyl.moka.queries.RepositoryReleasesQuery.Data.Repository.Releases.Node as ReleaseNode

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
                    avatarUrl = "https://avatars.githubusercontent.com/u/1776230?u=b99f29305efeed615ccbf10236f01a6a0bee739e&v=4",
                    login = "eneim",
                    url = "https://github.com/eneim"
                ),
                id = "MDU6SXNzdWU1MjEyNzQ1MjU=",
                number = 1,
                createdAt = Instant.parse("2019-11-12T01:24:24Z"),
                title = "[Feature request] Adding version distribution",
                closed = false
            ),
            IssueItem(
                actor = Actor(
                    avatarUrl = "https://avatars.githubusercontent.com/u/11989916?u=496f5fa6421a661441ef6467a909be31cb7be9c6&v=4",
                    login = "caiyoufei",
                    url = "https://github.com/caiyoufei"
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
                    avatarUrl = "https://avatars.githubusercontent.com/u/5749794?u=23b1d3479940253cd4848bf7c5b4520019a4c372&v=4",
                    login = "jshvarts",
                    url = "https://github.com/jshvarts"
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
                    avatarUrl = "https://avatars.githubusercontent.com/u/13329148?u=5f2267ec07a7e93d6281173e865faeb2363ff658&v=4",
                    login = "TonnyL",
                    url = "https://github.com/TonnyL"
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
                    avatarUrl = "https://avatars.githubusercontent.com/u/13329148?u=5f2267ec07a7e93d6281173e865faeb2363ff658&v=4",
                    login = "TonnyL",
                    url = "https://github.com/TonnyL"
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
        avatarUrl = "https://avatars.githubusercontent.com/u/352556?u=4c1ac7af0e0164029f36f8ecf262f3920d2e7b4f&v=4",
        login = "nickbutcher",
        url = "https://github.com/nickbutcher"
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
        avatarUrl = "https://avatars.githubusercontent.com/u/352556?u=4c1ac7af0e0164029f36f8ecf262f3920d2e7b4f&v=4",
        login = "nickbutcher",
        url = "https://github.com/nickbutcher"
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
            MilestonedEvent(
                actor = Actor(
                    avatarUrl = "https://avatars.githubusercontent.com/u/1833474?v=4",
                    login = "wasabeef",
                    url = "https://github.com/wasabeef"
                ),
                createdAt = Instant.parse("2020-03-12T21:48:09Z"),
                id = "",
                milestoneTitle = "v0.1.5"
            ),
            PullRequestCommit(
                commit = PullRequestTimelineItemCommit(
                    author = PullRequestTimelineItemGitActor(
                        avatarUrl = "https://avatars.githubusercontent.com/u/352556?v=4",
                        email = "nickbutcher@google.com",
                        name = "Nick Butcher",
                        user = User(
                            avatarUrl = "https://avatars.githubusercontent.com/u/352556?u=4c1ac7af0e0164029f36f8ecf262f3920d2e7b4f&v=4",
                            login = "nickbutcher",
                            name = "Nick Butcher",
                            url = "https://github.com/nickbutcher",
                            id = "MDQ6VXNlcjM1MjU1Ng=="
                        )
                    ),
                    committer = PullRequestTimelineItemGitActor(
                        avatarUrl = "https://avatars.githubusercontent.com/u/352556?v=4",
                        email = "nickbutcher@google.com",
                        name = "Nick Butcher",
                        user = User(
                            avatarUrl = "https://avatars.githubusercontent.com/u/352556?u=4c1ac7af0e0164029f36f8ecf262f3920d2e7b4f&v=4",
                            login = "nickbutcher",
                            name = "Nick Butcher",
                            url = "https://github.com/nickbutcher",
                            id = "MDQ6VXNlcjM1MjU1Ng=="
                        )
                    ),
                    message = "Update to snapshot 6994167.\n\nChange-Id: Ia634dfda0d83b5aaa187d6bb4a50cfb545a78841",
                    oid = "61a4cefac40aa542942dad4ae2e2962aef8e6ba0",
                    url = "https://github.com/android/compose-samples/commit/61a4cefac40aa542942dad4ae2e2962aef8e6ba0"
                ),
                id = "MDE3OlB1bGxSZXF1ZXN0Q29tbWl0NTI3NTI2MTA5OjYxYTRjZWZhYzQwYWE1NDI5NDJkYWQ0YWUyZTI5NjJhZWY4ZTZiYTA=",
                pullRequest = PullRequestTimelineItemPullRequest(
                    closed = false,
                    number = 293,
                    id = "MDExOlB1bGxSZXF1ZXN0NTI3NTI2MTA5",
                    state = PullRequestState.OPEN,
                    title = "[Jetsnack] Update SysUi controller to provide easier theming of status/nav bars",
                    url = "https://github.com/android/compose-samples/pull/293"
                ),
                url = "https://github.com/android/compose-samples/pull/293/commits/61a4cefac40aa542942dad4ae2e2962aef8e6ba0"
            ),
            IssueComment(
                author = Actor(
                    avatarUrl = "https://avatars.githubusercontent.com/u/227486?v=4",
                    login = "chrisbanes",
                    url = "https://github.com/chrisbanes"
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
                homepageUrl = "",
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
                    avatarUrl = "https://avatars.githubusercontent.com/u/13329148?u=5f2267ec07a7e93d6281173e865faeb2363ff658&v=4",
                    id = "MDQ6VXNlcjEzMzI5MTQ4",
                    login = "TonnyL",
                    resourcePath = "/TonnyL",
                    url = "https://github.com/TonnyL"
                ),
                parent = null,
                primaryLanguage = Language(
                    color = "#F18E33",
                    id = "MDg6TGFuZ3VhZ2UyNzI=",
                    name = "Kotlin"
                ),
                shortDescriptionHTML = "<g-emoji class=\"g-emoji\" alias=\"basketball\" fallback-src=\"https://github.githubassets.com/images/icons/emoji/unicode/1f3c0.png\">🏀</g-emoji> An Android app for dribbble.com",
                url = "https://github.com/TonnyL/Mango",
                viewerHasStarred = false,
                forksCount = 98,
                stargazersCount = 659
            ),
            RepositoryItem(
                description = "A command-line tool for browsing GitHub trending written by Rust.",
                descriptionHTML = "<div>A command-line tool for browsing GitHub trending written by Rust.</div>",
                homepageUrl = "",
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
                    avatarUrl = "https://avatars.githubusercontent.com/u/13329148?u=5f2267ec07a7e93d6281173e865faeb2363ff658&v=4",
                    id = "MDQ6VXNlcjEzMzI5MTQ4",
                    login = "TonnyL",
                    resourcePath = "/TonnyL",
                    url = "https://github.com/TonnyL"
                ),
                parent = null,
                primaryLanguage = Language(
                    color = "#dea584",
                    id = "MDg6TGFuZ3VhZ2UyNDk=",
                    name = "Rust"
                ),
                shortDescriptionHTML = "A command-line tool for browsing GitHub trending written by Rust.",
                url = "https://github.com/TonnyL/wukong",
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
                avatarUrl = "https://avatars.githubusercontent.com/u/3777021?u=8c2f8d46ac442b51f925e353bef7d80005a249e9&v=4",
                bio = "Passionate about Android development and product design. Ex Android Infrastructure Tech Lead @ Zhihu",
                bioHTML = "<div>Passionate about Android development and product design. Ex Android Infrastructure Tech Lead @ Zhihu</div>",
                id = "MDQ6VXNlcjM3NzcwMjE=",
                isViewer = false,
                login = "gejiaheng",
                name = "Jiaheng",
                url = "https://github.com/gejiaheng",
                viewerCanFollow = true,
                viewerIsFollowing = true
            ),
            UserItem(
                avatarUrl = "https://avatars.githubusercontent.com/u/352556?u=4c1ac7af0e0164029f36f8ecf262f3920d2e7b4f&v=4",
                bio = "Android designer and developer.",
                bioHTML = "<div>Android designer and developer.</div>",
                id = "MDQ6VXNlcjM1MjU1Ng==",
                isViewer = false,
                login = "nickbutcher",
                name = "Nick Butcher",
                url = "https://github.com/nickbutcher",
                viewerCanFollow = true,
                viewerIsFollowing = true
            )
        )

}

class SearchedOrganizationItemProvider : PreviewParameterProvider<SearchedOrganizationItem> {

    override val values: Sequence<SearchedOrganizationItem>
        get() = sequenceOf(
            SearchedOrganizationItem(
                avatarUrl = "https://avatars.githubusercontent.com/u/32689599?v=4",
                description = "",
                descriptionHTML = "<div></div>",
                id = "MDEyOk9yZ2FuaXphdGlvbjMyNjg5NTk5",
                isVerified = false,
                login = "android",
                name = "Android",
                url = "https://github.com/android",
                viewerIsAMember = false,
                websiteUrl = "https://d.android.com"
            ),
            SearchedOrganizationItem(
                avatarUrl = "https://avatars.githubusercontent.com/u/9919?v=4",
                description = "How people build software.",
                descriptionHTML = "<div>How people build software.</div>",
                id = "MDEyOk9yZ2FuaXphdGlvbjk5MTk=",
                isVerified = true,
                login = "github",
                name = "GitHub",
                url = "https://github.com/github",
                viewerIsAMember = false,
                websiteUrl = "https://github.com/about"
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

class CommitProvider : PreviewParameterProvider<Node> {

    override val values: Sequence<Node>
        get() = sequenceOf(
            Node(
                __typename = "",
                message = "update readme",
                messageHeadline = "update readme",
                committedDate = Instant.parse("2020-05-04T06:49:43Z"),
                committer = Node.Committer(
                    avatarUrl = "https://avatars.githubusercontent.com/u/13329148?v=4",
                    name = "Li Zhao Tai Lang",
                    user = Node.Committer.User(
                        avatarUrl = "https://avatars.githubusercontent.com/u/13329148?u=5f2267ec07a7e93d6281173e865faeb2363ff658&v=4",
                        name = "Li Zhao Tai Lang",
                        login = "TonnyL"
                    ),
                    __typename = "",
                ),
                author = Node.Author(
                    avatarUrl = "https://avatars.githubusercontent.com/u/13329148?v=4",
                    name = "Li Zhao Tai Lang",
                    user = Node.Author.User(
                        avatarUrl = "https://avatars.githubusercontent.com/u/13329148?u=5f2267ec07a7e93d6281173e865faeb2363ff658&v=4",
                        name = "Li Zhao Tai Lang",
                        login = "TonnyL"
                    ),
                    __typename = "",
                ),
                statusCheckRollup = Node.StatusCheckRollup(state = StatusState.SUCCESS),
                oid = ""
            )
        )

}

class RepositoryTopicProvider : PreviewParameterProvider<RepositoryTopic> {

    override val values: Sequence<RepositoryTopic>
        get() = sequenceOf(
            RepositoryTopic(
                id = "MDE1OlJlcG9zaXRvcnlUb3BpYzcyMzE3Mzc=",
                resourcePath = "/topics/kotlin-coroutines",
                topic = Topic(
                    id = "MDU6VG9waWNrb3RsaW4tY29yb3V0aW5lcw==",
                    name = "kotlin-coroutines",
                    viewerHasStarred = false
                ),
                url = "https://github.com/topics/kotlin-coroutines"
            )
        )

}

class UserProvider : PreviewParameterProvider<io.github.tonnyl.moka.data.User> {

    override val values: Sequence<io.github.tonnyl.moka.data.User>
        get() = sequenceOf(
            io.github.tonnyl.moka.data.User(
                avatarUrl = "https://avatars1.githubusercontent.com/u/13329148?u=0a5724e7c9f7d1cc4a5486ab7ab07abb7b8d7956&v=4",
                bio = "Rock/Post-rock/Electronic",
                bioHTML = "<div>Rock/Post-rock/Electronic</div>",
                company = null,
                companyHTML = "",
                createdAt = Instant.fromEpochMilliseconds(1436861097L),
                email = "lizhaotailang@gmail.com",
                id = "MDQ6VXNlcjEzMzI5MTQ4",
                isBountyHunter = false,
                isCampusExpert = false,
                isDeveloperProgramMember = true,
                isEmployee = false,
                isHireable = false,
                isSiteAdmin = false,
                isViewer = true,
                location = "Guangzhou",
                login = "TonnyL",
                name = "Li Zhao Tai Lang",
                resourcePath = "/TonnyL",
                status = UserStatus(
                    createdAt = Instant.fromEpochMilliseconds(1592643813L),
                    emoji = ":dart:",
                    expiresAt = null,
                    id = "3209515",
                    indicatesLimitedAvailability = false,
                    message = "Focusing",
                    updatedAt = Instant.fromEpochMilliseconds(1592643813L)
                ),
                updatedAt = Instant.fromEpochMilliseconds(1600415355L),
                url = "https://github.com/TonnyL",
                viewerCanFollow = false,
                viewerIsFollowing = false,
                websiteUrl = "https://tonnyl.io",
                twitterUsername = "@TonnyLZTL",
                repositoriesTotalCount = 22,
                followersTotalCount = 22,
                followingTotalCount = 22,
                starredRepositoriesTotalCount = 22,
                projectsTotalCount = 22,
                pinnedItems = mutableListOf(
                    RepositoryItem(
                        "📚 PaperPlane - An Android reading app, including articles from Zhihu Daily, Guokr Handpick and Douban Moment. ",
                        "<div>\n<g-emoji class=\"g-emoji\" alias=\"books\" fallback-src=\"https://github.githubassets.com/images/icons/emoji/unicode/1f4da.png\">📚</g-emoji> PaperPlane - An Android reading app, including articles from Zhihu Daily, Guokr Handpick and Douban Moment. </div>",
                        homepageUrl = null,
                        id = "MDEwOlJlcG9zaXRvcnk1NDIxMjM1NQ==",
                        isArchived = false,
                        isFork = false,
                        isLocked = false,
                        isMirror = false,
                        isPrivate = false,
                        mirrorUrl = null,
                        name = "PaperPlane",
                        nameWithOwner = "TonnyL/PaperPlane",
                        owner = RepositoryOwner(
                            avatarUrl = "https://avatars1.githubusercontent.com/u/13329148?u=0a5724e7c9f7d1cc4a5486ab7ab07abb7b8d7956&v=4",
                            id = "MDQ6VXNlcjEzMzI5MTQ4",
                            login = "TonnyL",
                            resourcePath = "/TonnyL",
                            url = "https://github.com/TonnyL"
                        ),
                        parent = null,
                        primaryLanguage = Language(
                            color = "#F18E33",
                            id = "MDg6TGFuZ3VhZ2UyNzI=",
                            name = "Kotlin"
                        ),
                        shortDescriptionHTML = "<g-emoji class=\"g-emoji\" alias=\"books\" fallback-src=\"https://github.githubassets.com/images/icons/emoji/unicode/1f4da.png\">📚</g-emoji> PaperPlane - An Android reading app, including articles from Zhihu Daily, Guokr Handpick and Douban Moment. ",
                        url = "https://github.com/TonnyL/PaperPlane",
                        viewerHasStarred = false,
                        forksCount = 22,
                        stargazersCount = 22
                    ),
                    Gist2(
                        createdAt = Instant.fromEpochMilliseconds(1573833346L),
                        description = "",
                        id = "MDQ6R2lzdGEzN2U5YTM3MGU0OGI5MDlhMzgzZDhlOTBiMzM5Y2Jk",
                        isFork = false,
                        isPublic = true,
                        name = "a37e9a370e48b909a383d8e90b339cbd",
                        owner = RepositoryOwner(
                            avatarUrl = "https://avatars3.githubusercontent.com/u/28293513?u=d7546e7c81e3ec8d39bac67dc7ac57e3fed1b244&v=4",
                            id = "MDQ6VXNlcjI4MjkzNTEz",
                            login = "lizhaotailang",
                            resourcePath = "/lizhaotailang",
                            url = "https://github.com/lizhaotailang"
                        ),
                        pushedAt = Instant.fromEpochMilliseconds(1573833347L),
                        resourcePath = "a37e9a370e48b909a383d8e90b339cbd",
                        updatedAt = Instant.fromEpochMilliseconds(1592647150),
                        url = "https://gist.github.com/a37e9a370e48b909a383d8e90b339cbd",
                        viewerHasStarred = true,
                        commentsTotalCount = 22,
                        forksTotalCount = 22,
                        stargazersTotalCount = 22,
                        firstFileName = "cryptocurrency_symbols.json",
                        firstFileText = "[\n  {\n    \"currency\": \"Bitcoin\",\n    \"abbreviation\": \"BTC\"\n  },\n  {\n    \"currency\": \"Ethereum\",\n    "
                    )
                )
            )
        )

}

class RepositoryProvider : PreviewParameterProvider<Repository> {

    val language = Language(
        color = "#F18E33",
        id = "MDg6TGFuZ3VhZ2UyNzI=",
        name = "Kotlin"
    )

    override val values: Sequence<Repository>
        get() = sequenceOf(
            Repository(
                codeOfConduct = null,
                createdAt = Instant.fromEpochMilliseconds(1458315345000L),
                defaultBranchRef = Ref(
                    id = "MDM6UmVmNTQyMTIzNTU6cmVmcy9oZWFkcy9tYXN0ZXI=",
                    name = "master",
                    prefix = "refs/heads/",
                    target = GitObject(
                        abbreviatedOid = "deabc06",
                        commitResourcePath = "/TonnyL/PaperPlane/commit/deabc062ec138e29f8b34bcea164c8ef49881175",
                        commitUrl = "https://github.com/TonnyL/PaperPlane/commit/deabc062ec138e29f8b34bcea164c8ef49881175",
                        id = "MDY6Q29tbWl0NTQyMTIzNTU6ZGVhYmMwNjJlYzEzOGUyOWY4YjM0YmNlYTE2NGM4ZWY0OTg4MTE3NQ==",
                        oid = "deabc062ec138e29f8b34bcea164c8ef49881175"
                    )
                ),
                description = "📚 PaperPlane - An Android reading app, including articles from Zhihu Daily, Guokr Handpick and Douban Moment. ",
                descriptionHTML = "<div>\n<g-emoji class=\"g-emoji\" alias=\"books\" fallback-src=\"https://github.githubassets.com/images/icons/emoji/unicode/1f4da.png\">📚</g-emoji> PaperPlane - An Android reading app, including articles from Zhihu Daily, Guokr Handpick and Douban Moment. </div>",
                diskUsage = 17162,
                forkCount = 22,
                hasIssuesEnabled = true,
                hasWikiEnabled = true,
                homepageUrl = "",
                id = "MDEwOlJlcG9zaXRvcnk1NDIxMjM1NQ==",
                isArchived = false,
                isFork = false,
                isLocked = false,
                isMirror = false,
                isPrivate = false,
                isTemplate = false,
                licenseInfo = License(
                    body = "                                 Apache License\n                           Version 2.0, January 2004\n                        http://www.apache.org/licenses/\n\n   TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION\n\n   1. Definitions.\n\n      \"License\" shall mean the terms and conditions for use, reproduction,\n      and distribution as defined by Sections 1 through 9 of this document.\n\n      \"Licensor\" shall mean the copyright owner or entity authorized by\n      the copyright owner that is granting the License.\n\n      \"Legal Entity\" shall mean the union of the acting entity and all\n      other entities that control, are controlled by, or are under common\n      control with that entity. For the purposes of this definition,\n      \"control\" means (i) the power, direct or indirect, to cause the\n      direction or management of such entity, whether by contract or\n      otherwise, or (ii) ownership of fifty percent (50%) or more of the\n      outstanding shares, or (iii) beneficial ownership of such entity.\n\n      \"You\" (or \"Your\") shall mean an individual or Legal Entity\n      exercising permissions granted by this License.\n\n      \"Source\" form shall mean the preferred form for making modifications,\n      including but not limited to software source code, documentation\n      source, and configuration files.\n\n      \"Object\" form shall mean any form resulting from mechanical\n      transformation or translation of a Source form, including but\n      not limited to compiled object code, generated documentation,\n      and conversions to other media types.\n\n      \"Work\" shall mean the work of authorship, whether in Source or\n      Object form, made available under the License, as indicated by a\n      copyright notice that is included in or attached to the work\n      (an example is provided in the Appendix below).\n\n      \"Derivative Works\" shall mean any work, whether in Source or Object\n      form, that is based on (or derived from) the Work and for which the\n      editorial revisions, annotations, elaborations, or other modifications\n      represent, as a whole, an original work of authorship. For the purposes\n      of this License, Derivative Works shall not include works that remain\n      separable from, or merely link (or bind by name) to the interfaces of,\n      the Work and Derivative Works thereof.\n\n      \"Contribution\" shall mean any work of authorship, including\n      the original version of the Work and any modifications or additions\n      to that Work or Derivative Works thereof, that is intentionally\n      submitted to Licensor for inclusion in the Work by the copyright owner\n      or by an individual or Legal Entity authorized to submit on behalf of\n      the copyright owner. For the purposes of this definition, \"submitted\"\n      means any form of electronic, verbal, or written communication sent\n      to the Licensor or its representatives, including but not limited to\n      communication on electronic mailing lists, source code control systems,\n      and issue tracking systems that are managed by, or on behalf of, the\n      Licensor for the purpose of discussing and improving the Work, but\n      excluding communication that is conspicuously marked or otherwise\n      designated in writing by the copyright owner as \"Not a Contribution.\"\n\n      \"Contributor\" shall mean Licensor and any individual or Legal Entity\n      on behalf of whom a Contribution has been received by Licensor and\n      subsequently incorporated within the Work.\n\n   2. Grant of Copyright License. Subject to the terms and conditions of\n      this License, each Contributor hereby grants to You a perpetual,\n      worldwide, non-exclusive, no-charge, royalty-free, irrevocable\n      copyright license to reproduce, prepare Derivative Works of,\n      publicly display, publicly perform, sublicense, and distribute the\n      Work and such Derivative Works in Source or Object form.\n\n   3. Grant of Patent License. Subject to the terms and conditions of\n      this License, each Contributor hereby grants to You a perpetual,\n      worldwide, non-exclusive, no-charge, royalty-free, irrevocable\n      (except as stated in this section) patent license to make, have made,\n      use, offer to sell, sell, import, and otherwise transfer the Work,\n      where such license applies only to those patent claims licensable\n      by such Contributor that are necessarily infringed by their\n      Contribution(s) alone or by combination of their Contribution(s)\n      with the Work to which such Contribution(s) was submitted. If You\n      institute patent litigation against any entity (including a\n      cross-claim or counterclaim in a lawsuit) alleging that the Work\n      or a Contribution incorporated within the Work constitutes direct\n      or contributory patent infringement, then any patent licenses\n      granted to You under this License for that Work shall terminate\n      as of the date such litigation is filed.\n\n   4. Redistribution. You may reproduce and distribute copies of the\n      Work or Derivative Works thereof in any medium, with or without\n      modifications, and in Source or Object form, provided that You\n      meet the following conditions:\n\n      (a) You must give any other recipients of the Work or\n          Derivative Works a copy of this License; and\n\n      (b) You must cause any modified files to carry prominent notices\n          stating that You changed the files; and\n\n      (c) You must retain, in the Source form of any Derivative Works\n          that You distribute, all copyright, patent, trademark, and\n          attribution notices from the Source form of the Work,\n          excluding those notices that do not pertain to any part of\n          the Derivative Works; and\n\n      (d) If the Work includes a \"NOTICE\" text file as part of its\n          distribution, then any Derivative Works that You distribute must\n          include a readable copy of the attribution notices contained\n          within such NOTICE file, excluding those notices that do not\n          pertain to any part of the Derivative Works, in at least one\n          of the following places: within a NOTICE text file distributed\n          as part of the Derivative Works; within the Source form or\n          documentation, if provided along with the Derivative Works; or,\n          within a display generated by the Derivative Works, if and\n          wherever such third-party notices normally appear. The contents\n          of the NOTICE file are for informational purposes only and\n          do not modify the License. You may add Your own attribution\n          notices within Derivative Works that You distribute, alongside\n          or as an addendum to the NOTICE text from the Work, provided\n          that such additional attribution notices cannot be construed\n          as modifying the License.\n\n      You may add Your own copyright statement to Your modifications and\n      may provide additional or different license terms and conditions\n      for use, reproduction, or distribution of Your modifications, or\n      for any such Derivative Works as a whole, provided Your use,\n      reproduction, and distribution of the Work otherwise complies with\n      the conditions stated in this License.\n\n   5. Submission of Contributions. Unless You explicitly state otherwise,\n      any Contribution intentionally submitted for inclusion in the Work\n      by You to the Licensor shall be under the terms and conditions of\n      this License, without any additional terms or conditions.\n      Notwithstanding the above, nothing herein shall supersede or modify\n      the terms of any separate license agreement you may have executed\n      with Licensor regarding such Contributions.\n\n   6. Trademarks. This License does not grant permission to use the trade\n      names, trademarks, service marks, or product names of the Licensor,\n      except as required for reasonable and customary use in describing the\n      origin of the Work and reproducing the content of the NOTICE file.\n\n   7. Disclaimer of Warranty. Unless required by applicable law or\n      agreed to in writing, Licensor provides the Work (and each\n      Contributor provides its Contributions) on an \"AS IS\" BASIS,\n      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or\n      implied, including, without limitation, any warranties or conditions\n      of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A\n      PARTICULAR PURPOSE. You are solely responsible for determining the\n      appropriateness of using or redistributing the Work and assume any\n      risks associated with Your exercise of permissions under this License.\n\n   8. Limitation of Liability. In no event and under no legal theory,\n      whether in tort (including negligence), contract, or otherwise,\n      unless required by applicable law (such as deliberate and grossly\n      negligent acts) or agreed to in writing, shall any Contributor be\n      liable to You for damages, including any direct, indirect, special,\n      incidental, or consequential damages of any character arising as a\n      result of this License or out of the use or inability to use the\n      Work (including but not limited to damages for loss of goodwill,\n      work stoppage, computer failure or malfunction, or any and all\n      other commercial damages or losses), even if such Contributor\n      has been advised of the possibility of such damages.\n\n   9. Accepting Warranty or Additional Liability. While redistributing\n      the Work or Derivative Works thereof, You may choose to offer,\n      and charge a fee for, acceptance of support, warranty, indemnity,\n      or other liability obligations and/or rights consistent with this\n      License. However, in accepting such obligations, You may act only\n      on Your own behalf and on Your sole responsibility, not on behalf\n      of any other Contributor, and only if You agree to indemnify,\n      defend, and hold each Contributor harmless for any liability\n      incurred by, or claims asserted against, such Contributor by reason\n      of your accepting any such warranty or additional liability.\n\n   END OF TERMS AND CONDITIONS\n\nAPPENDIX: How to apply the Apache License to your work.\n\n      To apply the Apache License to your work, attach the following\n      boilerplate notice, with the fields enclosed by brackets \"[]\"\n      replaced with your own identifying information. (Don't include\n      the brackets!)  The text should be enclosed in the appropriate\n      comment syntax for the file format. We also recommend that a\n      file or class name and description of purpose be included on the\n      same \"printed page\" as the copyright notice for easier\n      identification within third-party archives.\n\n   Copyright [yyyy] [name of copyright owner]\n\n   Licensed under the Apache License, Version 2.0 (the \"License\");\n   you may not use this file except in compliance with the License.\n   You may obtain a copy of the License at\n\n       http://www.apache.org/licenses/LICENSE-2.0\n\n   Unless required by applicable law or agreed to in writing, software\n   distributed under the License is distributed on an \"AS IS\" BASIS,\n   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n   See the License for the specific language governing permissions and\n   limitations under the License.\n",
                    conditions = listOf(
                        LicenseRule(
                            description = "A copy of the license and copyright notice must be included with the software.",
                            key = "include-copyright",
                            label = "License and copyright notice"
                        ),
                        // ... incomplete
                    ),
                    description = "A permissive license whose main conditions require preservation of copyright and license notices. Contributors provide an express grant of patent rights. Licensed works, modifications, and larger works may be distributed under different terms and without source code.",
                    featured = true,
                    hidden = false,
                    id = "MDc6TGljZW5zZTI=",
                    implementation = "Create a text file (typically named LICENSE or LICENSE.txt) in the root of your source code and copy the text of the license into the file.",
                    key = "apache-2.0",
                    limitations = listOf(
                        LicenseRule(
                            description = "This license explicitly states that it does NOT grant trademark rights, even though licenses without such a statement probably do not grant any implicit trademark rights.",
                            key = "trademark-use",
                            label = "Trademark use"
                        ),
                        // ... incomplete
                    ),
                    name = "Apache License 2.0",
                    nickname = null,
                    permissions = listOf(
                        LicenseRule(
                            description = "This software and derivatives may be used for commercial purposes.",
                            key = "commercial-use",
                            label = "Commercial use"
                        ),
                        // ... incomplete
                    ),
                    pseudoLicense = false,
                    spdxId = "Apache-2.0",
                    url = "http://choosealicense.com/licenses/apache-2.0/"
                ),
                lockReason = null,
                mergeCommitAllowed = true,
                mirrorUrl = null,
                name = "PaperPlane",
                nameWithOwner = "TonnyL/PaperPlane",
                openGraphImageUrl = "https://avatars3.githubusercontent.com/u/13329148?s=400&u=0a5724e7c9f7d1cc4a5486ab7ab07abb7b8d7956&v=4",
                owner = RepositoryOwner(
                    avatarUrl = "https://avatars1.githubusercontent.com/u/13329148?u=0a5724e7c9f7d1cc4a5486ab7ab07abb7b8d7956&v=4",
                    id = "MDQ6VXNlcjEzMzI5MTQ4",
                    login = "TonnyL",
                    resourcePath = "/TonnyL",
                    url = "https://github.com/TonnyL"
                ),
                primaryLanguage = language,
                pushedAt = Instant.fromEpochMilliseconds(1528288541000),
                rebaseMergeAllowed = true,
                resourcePath = "/TonnyL/PaperPlane",
                shortDescriptionHTML = "<g-emoji class=\"g-emoji\" alias=\"books\" fallback-src=\"https://github.githubassets.com/images/icons/emoji/unicode/1f4da.png\">📚</g-emoji> PaperPlane - An Android reading app, including articles from Zhihu Daily, Guokr Handpick and Douban Moment. ",
                squashMergeAllowed = true,
                sshUrl = "git@github.com:TonnyL/PaperPlane.git",
                updatedAt = Instant.fromEpochMilliseconds(1601370442000),
                url = "https://github.com/TonnyL/PaperPlane",
                usesCustomOpenGraphImage = false,
                viewerCanAdminister = true,
                viewerCanSubscribe = true,
                viewerCanUpdateTopics = true,
                viewerHasStarred = false,
                viewerPermission = RepositoryPermission.ADMIN,
                viewerSubscription = SubscriptionState.SUBSCRIBED,
                ownerName = "Li Zhao Tai Lang",
                isViewer = true,
                viewerIsFollowing = false,
                viewerCanFollow = true, // fake value
                forksCount = 22,
                stargazersCount = 22,
                issuesCount = 22,
                pullRequestsCount = 22,
                watchersCount = 22,
                releasesCount = 22,
                branchCount = 22,
                commitsCount = 22,
                topics = listOf(
                    RepositoryTopic(
                        id = "MDE1OlJlcG9zaXRvcnlUb3BpYzY2NDc5Nw==",
                        resourcePath = "/topics/zhihu",
                        topic = Topic(
                            id = "MDU6VG9waWN6aGlodQ==",
                            name = "zhihu",
                            viewerHasStarred = false
                        ),
                        url = "https://github.com/topics/zhihu"
                    ),
                    RepositoryTopic(
                        id = "",
                        resourcePath = "/topics/android",
                        topic = Topic(
                            id = "",
                            name = "android",
                            viewerHasStarred = false
                        ),
                        url = "https://github.com/topics/android"
                    ),
                    RepositoryTopic(
                        id = "",
                        resourcePath = "/topics/ios",
                        topic = Topic(
                            id = "",
                            name = "ios",
                            viewerHasStarred = false
                        ),
                        url = "https://github.com/topics/ios"
                    ),
                ),
                languagesTotalSize = 22,
                languages = listOf(language),
                languageEdges = listOf(1),
                otherLanguagePercentage = 0.0
            )
        )

}

class IssueProvider : PreviewParameterProvider<Issue> {

    override val values: Sequence<Issue>
        get() = sequenceOf(
            Issue(
                activeLockReason = null,
                author = Actor(
                    avatarUrl = "https://avatars.githubusercontent.com/u/1998880?v=4",
                    login = "kevinunic",
                    url = "https://github.com/kevinunic"
                ),
                authorAssociation = CommentAuthorAssociation.NONE,
                body = "![image](https://user-images.githubusercontent.com/1998880/69028539-42376280-0a0d-11ea-9112-4e994e257b52.png)\r\n",
                bodyHTML = "<p><a target=\"_blank\" rel=\"noopener noreferrer\" href=\"https://user-images.githubusercontent.com/1998880/69028539-42376280-0a0d-11ea-9112-4e994e257b52.png\"><img src=\"https://user-images.githubusercontent.com/1998880/69028539-42376280-0a0d-11ea-9112-4e994e257b52.png\" alt=\"image\" style=\"max-width:100%;\"></a></p>",
                bodyText = "",
                closed = true,
                closedAt = Instant.parse("2019-11-19T05:12:40Z"),
                createdAt = Instant.parse("2019-11-18T06:11:11Z"),
                createdViaEmail = false,
                id = "MDU6SXNzdWU1MjQxNTcyMzA=",
                editor = null,
                includesCreatedEdit = false,
                lastEditedAt = null,
                locked = false,
                number = 3,
                publishedAt = Instant.parse("2019-11-18T06:11:11Z"),
                reactionGroups = null,
                resourcePath = "/wasabeef/droid/issues/3",
                state = IssueState.CLOSED,
                title = "7.1.2 api level should be 25, but now it's 15",
                updatedAt = Instant.parse("2019-11-19T05:12:40Z"),
                url = "https://github.com/wasabeef/droid/issues/3",
                viewerCanReact = true,
                viewerCanSubscribe = true,
                viewerCanUpdate = false,
                viewerCannotUpdateReasons = listOf(CommentCannotUpdateReason.INSUFFICIENT_ACCESS),
                viewerDidAuthor = false,
                milestone = null,
                viewerSubscription = SubscriptionState.UNSUBSCRIBED
            )
        )

}

class PullRequestProvider : PreviewParameterProvider<PullRequest> {

    override val values: Sequence<PullRequest>
        get() = sequenceOf(
            PullRequest(
                activeLockReason = null,
                additions = 19,
                author = Actor(
                    avatarUrl = "https://avatars.githubusercontent.com/u/13329148?u=5f2267ec07a7e93d6281173e865faeb2363ff658&v=4",
                    login = "TonnyL",
                    url = "https://github.com/TonnyL"
                ),
                authorAssociation = CommentAuthorAssociation.CONTRIBUTOR,
                baseRef = Ref(
                    id = "MDM6UmVmMjIwNzUwMzU3OnJlZnMvaGVhZHMvbWFzdGVy",
                    name = "master",
                    prefix = "refs/heads/",
                    target = GitObject(
                        abbreviatedOid = "2060b21",
                        commitResourcePath = "/wasabeef/droid/commit/2060b21c580316d3789b2397dc54b70e5dfc37f0",
                        commitUrl = "https://github.com/wasabeef/droid/commit/2060b21c580316d3789b2397dc54b70e5dfc37f0",
                        id = "MDY6Q29tbWl0MjIwNzUwMzU3OjIwNjBiMjFjNTgwMzE2ZDM3ODliMjM5N2RjNTRiNzBlNWRmYzM3ZjA=",
                        oid = "2060b21c580316d3789b2397dc54b70e5dfc37f0"
                    )
                ),
                baseRefName = "master",
                baseRefOid = "cb0db60705021ae420051b7ca1d05cc20e1647bd",
                body = "1. update reqwest dependency to version 0.10;\r\n2. handle network request / json-parsing errors;\r\n3. reformat some codes.",
                bodyHTML = "<ol>\n<li>update reqwest dependency to version 0.10;</li>\n<li>handle network request / json-parsing errors;</li>\n<li>reformat some codes.</li>\n</ol>",
                bodyText = "update reqwest dependency to version 0.10;\nhandle network request / json-parsing errors;\nreformat some codes.",
                changedFiles = 2,
                closed = true,
                closedAt = Instant.parse("2020-03-12T21:48:10Z"),
                createdAt = Instant.parse("2020-03-12T14:49:22Z"),
                createdViaEmail = false,
                deletions = 6,
                editor = null,
                headRefName = "master",
                headRefOid = "fdc468a245b7d0f29ae0fd4627de1509b1190962",
                id = "MDExOlB1bGxSZXF1ZXN0Mzg3Mjg4MjUz",
                includesCreatedEdit = false,
                isCrossRepository = true,
                lastEditedAt = null,
                locked = false,
                maintainerCanModify = false,
                merged = true,
                mergedAt = Instant.parse("2020-03-12T21:48:09Z"),
                number = 5,
                reactionGroups = null,
                resourcePath = "/wasabeef/droid/pull/5",
                headRef = null,
                milestone = null,
                headRepositoryOwner = RepositoryOwner(
                    avatarUrl = "https://avatars.githubusercontent.com/u/13329148?u=5f2267ec07a7e93d6281173e865faeb2363ff658&v=4",
                    id = "MDQ6VXNlcjEzMzI5MTQ4",
                    login = "TonnyL",
                    resourcePath = "/TonnyL",
                    url = "https://github.com/TonnyL"
                ),
                publishedAt = Instant.parse("2020-03-12T14:49:22Z"),
                mergedBy = Actor(
                    avatarUrl = "https://avatars.githubusercontent.com/u/1833474?v=4",
                    login = "wasabeef",
                    url = "https://github.com/wasabeef"
                ),
                permalink = "https://github.com/wasabeef/droid/pull/5",
                revertResourcePath = "/wasabeef/droid/pull/5/revert",
                revertUrl = "https://github.com/wasabeef/droid/pull/5/revert",
                state = PullRequestState.MERGED,
                title = "handle network request / json-parsing errors",
                updatedAt = Instant.parse("2020-03-12T22:00:51Z"),
                url = "https://github.com/wasabeef/droid/pull/5",
                viewerCanApplySuggestion = false,
                viewerCanReact = true,
                viewerCanSubscribe = true,
                viewerCanUpdate = true,
                viewerCannotUpdateReasons = emptyList(),
                viewerDidAuthor = true,
                viewerSubscription = SubscriptionState.SUBSCRIBED
            )
        )

}

class ReleaseProvider : PreviewParameterProvider<ReleaseListItem> {

    override val values: Sequence<ReleaseListItem>
        get() = sequenceOf(
            ReleaseNode(
                __typename = "Release",
                createdAt = Instant.parse("2021-06-25T22:13:53Z"),
                name = "v0.5.0-build228",
                tagName = "v0.5.0-build228",
                isDraft = false,
                isLatest = false,
                isPrerelease = true,
                url = "https://github.com/JetBrains/compose-jb/releases/tag/v0.5.0-build228"
            )
        )

}

class CommitResponseProvider : PreviewParameterProvider<CommitResponse> {

    override val values: Sequence<CommitResponse>
        get() = sequenceOf(
            CommitResponse(
                url = "https://api.github.com/repos/TonnyL/PaperPlane/commits/deabc062ec138e29f8b34bcea164c8ef49881175",
                sha = "deabc062ec138e29f8b34bcea164c8ef49881175",
                nodeId = "MDY6Q29tbWl0NTQyMTIzNTU6ZGVhYmMwNjJlYzEzOGUyOWY4YjM0YmNlYTE2NGM4ZWY0OTg4MTE3NQ==",
                htmlUrl = "https://github.com/TonnyL/PaperPlane/commit/deabc062ec138e29f8b34bcea164c8ef49881175",
                commentsUrl = "https://api.github.com/repos/TonnyL/PaperPlane/commits/deabc062ec138e29f8b34bcea164c8ef49881175/comments",
                commit = Commit(
                    author = CommitCommitter(
                        name = "tonnyl",
                        email = "lizhaotailang@gmail.com",
                        date = Instant.parse("2018-06-06T12:35:32Z")
                    ),
                    committer = CommitCommitter(
                        name = "tonnyl",
                        email = "lizhaotailang@gmail.com",
                        date = Instant.parse("2018-06-06T12:35:32Z")
                    ),
                    message = "feat: replace product sans with roboto",
                    tree = CommitTree(
                        sha = "1d92e6d02c2b3460cc497f5908ac99be792099eb",
                        url = "https://api.github.com/repos/TonnyL/PaperPlane/git/trees/1d92e6d02c2b3460cc497f5908ac99be792099eb"
                    ),
                    url = "https://api.github.com/repos/TonnyL/PaperPlane/git/commits/deabc062ec138e29f8b34bcea164c8ef49881175",
                    verification = CommitVerification(
                        verified = false,
                        reason = "unsigned",
                        signature = null,
                        payload = null
                    ),
                    commentCount = 0
                ),
                author = EventActor(
                    id = 13329148,
                    login = "TonnyL",
                    avatarUrl = "https://avatars.githubusercontent.com/u/13329148?v=4",
                    htmlUrl = "https://github.com/TonnyL",
                    type = "User"
                ),
                committer = EventActor(
                    id = 13329148,
                    login = "TonnyL",
                    avatarUrl = "https://avatars.githubusercontent.com/u/13329148?v=4",
                    htmlUrl = "https://github.com/TonnyL",
                    type = "User"
                ),
                parents = listOf(
                    CommitParent(
                        sha = "7813c3e100d1f4b268e669b3d1a7e1b154c6b2b9",
                        url = "https://api.github.com/repos/TonnyL/PaperPlane/commits/7813c3e100d1f4b268e669b3d1a7e1b154c6b2b9",
                    )
                ),
                stats = CommitStats(
                    total = 9,
                    additions = 5,
                    deletions = 4
                ),
                files = emptyList()
            )
        )

}

class CommitFileProvider : PreviewParameterProvider<CommitFile> {

    override val values: Sequence<CommitFile>
        get() = sequenceOf(
            CommitFile(
                filename = "app/build.gradle",
                status = "modified",
                additions = 1,
                deletions = 0,
                changes = 1,
                blobUrl = "https://github.com/TonnyL/PaperPlane/blob/deabc062ec138e29f8b34bcea164c8ef49881175/app/build.gradle",
                rawUrl = "https://github.com/TonnyL/PaperPlane/raw/deabc062ec138e29f8b34bcea164c8ef49881175/app/build.gradle",
                patch = "@@ -5,6 +5,7 @@ apply plugin: 'kotlin-kapt'"
            )
        )

}

class TreeEntryProvider : PreviewParameterProvider<TreeEntry> {

    override val values: Sequence<TreeEntry>
        get() = sequenceOf(
            Entry(
                __typename = "",
                mode = 16384,
                name = "settings.gradle",
                object_ = Entry.CommitObject(
                    __typename = "",
                    abbreviatedOid = "e7b4def",
                    commitResourcePath = "/TonnyL/PaperPlane/commit/e7b4def49cb53d9aa04228dd3edb14c9e635e003",
                    commitUrl = "https://github.com/TonnyL/PaperPlane/commit/e7b4def49cb53d9aa04228dd3edb14c9e635e003",
                    id = "MDQ6QmxvYjU0MjEyMzU1OmU3YjRkZWY0OWNiNTNkOWFhMDQyMjhkZDNlZGIxNGM5ZTYzNWUwMDM=",
                    oid = "e7b4def49cb53d9aa04228dd3edb14c9e635e003",
                    history = Entry.CommitObject.History(totalCount = 1)
                ),
                oid = "e7b4def49cb53d9aa04228dd3edb14c9e635e003",
                type = "blob"
            )
        )

}
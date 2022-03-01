//
//  TimelineScreen.swift
//  ios
//
//  Created by lizhaotailang on 2022/2/27.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import SwiftUIX
import common

struct TimelineScreen: View {
    
    @ObservedObject var viewModel = TimelineViewModel()
    
    var body: some View {
        NavigationView {
            let data = viewModel.eventsResource?.data?.array ?? []
            let status = viewModel.eventsResource?.status
            
            ZStack {
                if !data.isEmpty {
                    ScrollView {
                        LazyVStack(alignment: .leading) {
                            ForEach(0..<data.count + 1, id: \.self) { i in
                                makeItemView(index: i, data: data, status: status) {
                                    viewModel.loadData(forceRefresh: false)
                                }
                            }
                        }
                    }
                } else if status == .error {
                    EmptyScreen() {
                        viewModel.loadData(forceRefresh: true)
                    }
                } else if status == .loading {
                    ActivityIndicator()
                        .animated(true)
                        .style(.regular)
                } else {
                    EmptyScreen(msgString: NSLocalizedString("Common.NoDataFound", comment: ""), actionString: "Common.Retry") {
                        viewModel.loadData(forceRefresh: true)
                    }
                }
            }
            .navigationTitle(NSLocalizedString("MainTab.Timeline", comment: ""))
        }
    }
    
}

private func makeItemView(
    index: Int,
    data: [Event],
    status: Status?,
    loadMore: @escaping () -> Void
) -> some View {
    Group {
        if index == data.count {
            if status == nil
                || data.isEmpty {
                EmptyView()
            } else {
                ItemLoadingStateView(status: status!)
            }
        } else {
            EventItem(
                event: data[index],
                index: index,
                totalCount: data.count
            ) {
                loadMore()
            }
        }
    }
}

struct EventItem: View {
    
    let event: Event
    let login: String
    let repoName: String
    let index: Int
    let totalCount: Int
    let loadMore: () -> Void
    
    init(
        event: Event,
        index: Int,
        totalCount: Int,
        loadMore: @escaping () -> Void
    ) {
        self.index = index
        self.totalCount = totalCount
        self.event = event
        self.loadMore = loadMore
        let fullName = event.repo?.fullName?.split(separator: "/") ?? []
        if fullName.count < 2 {
            login = ""
            repoName = ""
        } else {
            login = String(fullName[0])
            repoName = String(fullName[1])
        }
    }
    
    var body: some View {
        NavigationLink(destination: RepositoryScreen(login: login, repoName: repoName)) {
            HStack(alignment: .top, spacing: 10) {
                AvatarView(url: event.actor.avatarUrl)
                VStack(alignment: .leading, spacing: 5) {
                    Text(makeContentString())
                        .multilineTextAlignment(.leading)
                    Text(String(event.createdAt.formattedString))
                        .font(.caption)
                }
                Spacer()
            }
            .padding(10)
        }
        .buttonStyle(PlainButtonStyle())
        .onAppear {
            if index == totalCount - 1 {
                loadMore()
            }
        }
    }
    
    func makeContentString() -> NSMutableAttributedString {
        let content = NSMutableAttributedString.init(string: event.actor.login)
        content.addAttribute(NSAttributedString.Key.foregroundColor, value: UIColor.systemBlue, range: NSRange(location: 0, length: content.length))
        
        func appendRepoNameIfNeeded(includingAt: Bool = true) {
            if event.repo != nil {
                if includingAt {
                    content.append(NSAttributedString(string: NSLocalizedString("Timeline.Event.At", comment: "")))
                }
                
                let repoName = event.repo!.name
                content.append(NSAttributedString(string: repoName))
                content.addAttribute(NSAttributedString.Key.foregroundColor, value: UIColor.systemBlue, range: NSRange(location: content.length - repoName.count, length: repoName.count))
            }
        }
        
        func appendColoredString(string: String) {
            content.append(NSAttributedString(string: string))
            content.addAttribute(NSAttributedString.Key.foregroundColor, value: UIColor.systemBlue, range: NSRange(location: content.length - string.count, length: string.count))
        }
        
        func appendIssueOrPrNumber(isIssue: Bool) {
            if isIssue {
                if event.payload?.issue != nil {
                    let number = String(format: NSLocalizedString("Issue.Pr.Number", comment: ""), (event.payload?.issue?.number)!)
                    content.append(NSAttributedString(string: number))
                }
            } else {
                if event.payload?.pullRequest != nil {
                    let number = String(format: NSLocalizedString("Issue.Pr.Number", comment: ""), (event.payload?.pullRequest?.number)!)
                    content.append(NSAttributedString(string: number))
                }
            }
        }
        
        switch event.type {
        case Event.companion.WATCH_EVENT:
            content.append(NSAttributedString(string: NSLocalizedString("Timeline.Event.Starred", comment: "")))
            
            appendRepoNameIfNeeded(includingAt: false)
            
            // final string example: github starred github/github
            break
        case Event.companion.CREATE_EVENT:
            let createdType: String
            switch event.payload?.refType {
            case "repository":
                createdType = NSLocalizedString("Timeline.Event.Repository", comment: "")
                break
            case "branch":
                createdType = NSLocalizedString("Timeline.Event.Branch", comment: "")
                break
            default: // including "tag"
                createdType = NSLocalizedString("Timeline.Event.Tag", comment: "")
                break
            }
            
            content.append(NSAttributedString(string: NSLocalizedString("Timeline.Event.Created", comment: "")))
            content.append(NSAttributedString(string: createdType))
            
            if event.payload?.ref != nil {
                content.append(NSAttributedString(string: event.payload!.ref!))
            }
            
            appendRepoNameIfNeeded()
            
            // final string example: github created repository github/github
            break
        case Event.companion.COMMIT_COMMENT_EVENT:
            content.append(NSAttributedString(string: NSLocalizedString("Timeline.Event.CommentOnCommit", comment: "")))
            
            if event.payload?.commitComment?.commitId != nil {
                content.append(NSAttributedString(string: StringExtensionsKt.shortOid(event.payload?.commitComment?.commitId)))
            }
            
            appendRepoNameIfNeeded()
            
            // final string example: github commented on commit ec7a2824 at github/github
            break
        case Event.companion.DOWNLOAD_EVENT:
            content.append(NSAttributedString(string: NSLocalizedString("Timeline.Event.Downloaded", comment: "")))
            
            if event.payload?.download?.name != nil {
                content.append(NSAttributedString(string: (event.payload?.download?.name)!))
            }
            
            appendRepoNameIfNeeded()
            
            // final string example: github downloaded logo.jpe at github/github
            break
        case Event.companion.FOLLOW_EVENT:
            content.append(NSAttributedString(string: NSLocalizedString("Timeline.Event.Followed", comment: "")))
            
            if event.payload?.target?.login != nil {
                appendColoredString(string: (event.payload?.target?.login)!)
            }
            
            // final string example: github followed octocat
            break
        case Event.companion.FORK_EVENT:
            content.append(NSAttributedString(string: NSLocalizedString("Timeline.Event.Forked", comment: "")))
            
            appendRepoNameIfNeeded(includingAt: false)
            
            content.append(NSAttributedString(string: NSLocalizedString("Timeline.Event.To", comment: "")))
            
            if event.payload?.forkee != nil {
                appendColoredString(string: ((event.payload?.forkee?.fullName ?? event.payload?.forkee?.name)!))
            }
            
            // final string example: github forked actocat/Hello-World to github/Hello-World
            break
        case Event.companion.GIST_EVENT:
            let action: String
            if event.payload?.action == "create" {
                action = NSLocalizedString("Timeline.Event.Created", comment: "")
            } else { // including "update".
                action = NSLocalizedString("Timeline.Event.Updated", comment: "")
            }
            
            content.append(NSAttributedString(string: action))
            content.append(NSAttributedString(string: NSLocalizedString("Timeline.Event.Gist", comment: "")))
            
            if event.payload?.gist != nil {
                appendColoredString(string: (event.payload?.gist?.description_)!)
            }
            
            // final string example: github created Gist Hello World Examples
            break
        case Event.companion.GOLLUM_EVENT:
            let action: String
            if event.payload?.pages?.first?.action == "created" {
                action = NSLocalizedString("Timeline.Event.Created", comment: "")
            } else {
                action = NSLocalizedString("Timeline.Event.Edited", comment: "")
            }
            
            content.append(NSAttributedString(string: action))
            content.append(NSAttributedString(string: NSLocalizedString("Timeline.Event.Gollum", comment: "")))
            
            appendRepoNameIfNeeded(includingAt: false)
            
            // final string example: github edit a wiki page at github/github
            break
        case Event.companion.ISSUE_COMMENT_EVENT:
            let action: String
            switch event.payload?.action { // Can be one of "created", "edited", or "deleted".
            case "created":
                action = NSLocalizedString("Timeline.Event.Created", comment: "")
            case "edited":
                action = NSLocalizedString("Timeline.Event.Edited", comment: "")
            default:
                action = NSLocalizedString("Timeline.Event.Deleted", comment: "")
            }
            
            content.append(NSAttributedString(string: action))
            content.append(NSAttributedString(string: NSLocalizedString("Timeline.Event.Issue.Comment", comment: "")))
            
            appendIssueOrPrNumber(isIssue: true)
            
            appendRepoNameIfNeeded()
            
            // final string example: github commented on issue #1 at github/github
            break
        case Event.companion.ISSUES_EVENT:
            let action: String
            switch event.payload?.action {
            case "opened":
                action = "Timeline.Event.Opened"
                break
            case "edited":
                action = "Timeline.Event.Edited"
                break
            case "deleted":
                action = "Timeline.Event.Deleted"
                break
            case "transferred":
                action = "Timeline.Event.Transferred"
                break
            case "pinned":
                action = "Timeline.Event.Pinned"
                break
            case "unpinned":
                action = "Timeline.Event.Unpinned"
                break
            case "closed":
                action = "Timeline.Event.Closed"
                break
            case "reopened":
                action = "Timeline.Event.Reopened"
                break
            case "assigned":
                action = "Timeline.Event.Assigned"
                break
            case "unassigned":
                action = "Timeline.Event.Unassigned"
                break
            case "labeled":
                action = "Timeline.Event.Labeled"
                break
            case "unlabeled":
                action = "Timeline.Event.Unlabeled"
                break
            case "milestoned":
                action = "Timeline.Event.Milestoned"
                break
            default: // including "demilestoned"
                action = "Timeline.Event.Demilestoned"
                break
            }
            
            content.append(NSAttributedString(string: NSLocalizedString(action, comment: "")))
            content.append(NSAttributedString(string: NSLocalizedString("Timeline.Event.Issue", comment: "")))
            
            appendIssueOrPrNumber(isIssue: true)
            
            appendRepoNameIfNeeded()
            
            // final string example: github created issue #1 at github/github
            break
        case Event.companion.MEMBER_EVENT:
            let actionString: String
            let toOrFromString: String
            
            switch event.payload?.action {
            case "added":
                actionString = "Timeline.Event.Added"
                toOrFromString = "Timeline.Event.To"
                break
            case "deleted":
                actionString = "Timeline.Event.Deleted"
                toOrFromString = "Timeline.Event.From"
            default: // including "edited"
                actionString = "Timeline.Event.Edited"
                toOrFromString = "Timeline.Event.At"
                break
            }
            
            content.append(NSAttributedString(string: NSLocalizedString(actionString, comment: "")))
            
            if event.payload?.member != nil {
                appendColoredString(string: (event.payload?.member?.login)!)
            }
            
            content.append(NSAttributedString(string: NSLocalizedString(toOrFromString, comment: "")))
            
            appendRepoNameIfNeeded(includingAt: false)
            
            // final string example: github added octocat at github/github
            break
        case Event.companion.PUBLIC_EVENT:
            content.append(NSAttributedString(string: NSLocalizedString("Timeline.Event.OpenSourced", comment: "")))
            
            appendRepoNameIfNeeded(includingAt: false)
            
            // final string example: github open-sourced github/github
            break
        case Event.companion.PULL_REQUEST_EVENT:
            let action: String
            switch event.payload?.action {
            case "assigned":
                action = "Timeline.Event.Assigned"
                break
            case "unassigned":
                action = "Timeline.Event.Unassigned"
                break
            case "review_requested":
                action = "Timeline.Event.ReviewRequested"
                break
            case "review_request_removed":
                action = "Timeline.Event.ReviewRequestRemoved"
                break
            case "labeled":
                action = "Timeline.Event.Labeled"
                break
            case "unlabeled":
                action = "Timeline.Event.Unlabeled"
                break
            case "opened":
                action = "Timeline.Event.Opened"
                break
            case "edited":
                action = "Timeline.Event.Edited"
                break
            case "closed":
                action = "Timeline.Event.Closed"
                break
            case "reopened":
                action = "Timeline.Event.Reopened"
                break
            default: // including "synchronized"
                action = "Timeline.Event.Synchronized"
                break
            }
            
            content.append(NSAttributedString(string:  NSLocalizedString(action, comment: "")))
            content.append(NSAttributedString(string:  NSLocalizedString("Timeline.Event.PullRequest", comment: "")))
            
            appendIssueOrPrNumber(isIssue: false)
            
            appendRepoNameIfNeeded()
            
            // final string example: github opened a pull request #1 at github/github
            break
        case Event.companion.PULL_REQUEST_REVIEW_COMMENT_EVENT:
            let action: String
            switch event.payload?.action {
            case "created":
                action = "Timeline.Event.Created"
                break
            case "edited":
                action = "Timeline.Event.Edited"
                break
            default: // including "deleted"
                action = "Timeline.Event.Deleted"
                break
            }
            
            content.append(NSAttributedString(string: NSLocalizedString(action, comment: "")))
            content.append(NSAttributedString(string: NSLocalizedString("Timeline.Event.PullRequest.ReviewComment", comment: "")))
            
            appendIssueOrPrNumber(isIssue: false)
            
            appendRepoNameIfNeeded()
            
            // final string example: github commented on pull request #1 at github/github
            break
        case Event.companion.PULL_REQUEST_REVIEW_EVENT:
            let action: String
            switch event.payload?.action {
            case "submitted":
                action = "Timeline.Event.Submitted"
                break
            case "edited":
                action = "Timeline.Event.Edited"
                break
            default: // including "dismissed"
                action = "Timeline.Event.Dismissed"
                break
            }
            
            content.append(NSAttributedString(string: NSLocalizedString(action, comment: "")))
            content.append(NSAttributedString(string: NSLocalizedString("Timeline.Event.PullRequest.Review", comment: "")))
            
            appendIssueOrPrNumber(isIssue: false)
            
            appendRepoNameIfNeeded()
            
            // final string example: github reviewed pull request #1 at github/github
            break
        case Event.companion.REPOSITORY_EVENT:
            let action: String
            switch event.payload?.action {
            case "created":
                action = "Timeline.Event.Created"
                break
            case "deleted":
                action = "Timeline.Event.Deleted"
                break
            case "archived":
                action = "Timeline.Event.Archived"
                break
            case "unarchived":
                action = "Timeline.Event.Unarchived"
                break
            case "publicized":
                action = "Timeline.Event.Publicized"
                break
            default: // including "privatized"
                action = "Timeline.Event.Privatized"
                break
            }
            
            content.append(NSAttributedString(string: NSLocalizedString(action, comment: "")))
            content.append(NSAttributedString(string: NSLocalizedString("Timeline.Event.Repository", comment: "")))
            
            appendRepoNameIfNeeded(includingAt: false)
            
            // final string example: github created repository github/github
            break
        case Event.companion.PUSH_EVENT:
            content.append(NSAttributedString(string: NSLocalizedString("Timeline.Event.PushedTo", comment: "")))
            
            let ref = event.payload?.ref
            if ref?.starts(with: "refs/heads/") == true {
                content.append(NSAttributedString(string:  "refs/heads/"))
            } else if ref?.starts(with: "refs/tags/") == true {
                content.append(NSAttributedString(string:  "refs/tags/"))
            } else {
                content.append(NSAttributedString(string: ref ?? ""))
            }
            
            appendRepoNameIfNeeded()
            
            // final string example: github pushed 1 commit(s) to github/github
            break
        case Event.companion.TEAM_ADD_EVENT:
            content.append(NSAttributedString(string: NSLocalizedString("Timeline.Event.AddedTeamRepository", comment: "")))
            
            appendRepoNameIfNeeded(includingAt: false)
            
            // final string example: github added repository github/github
            break
        case Event.companion.DELETE_EVENT:
            content.append(NSAttributedString(string: NSLocalizedString("Timeline.Event.Deleted", comment: "")))
            
            let action: String
            if event.payload?.refType == "branch" {
                action = "Timeline.Event.Branch"
            } else {
                action = "Timeline.Event.Tag"
            }
            
            content.append(NSAttributedString(string: NSLocalizedString(action, comment: "")))
            
            if event.payload?.ref != nil {
                content.append(NSAttributedString(string: (event.payload?.ref)!))
            }
            
            appendRepoNameIfNeeded()
            
            // final string example: github deleted branch dev at github/github
            break
        case Event.companion.RELEASE_EVENT:
            content.append(NSAttributedString(string: NSLocalizedString("Timeline.Event.Released", comment: "")))
            
            if event.payload?.eventRelease != nil {
                appendColoredString(string: (event.payload?.eventRelease?.tagName)!)
            }
            
            appendRepoNameIfNeeded()
            
            break
        case Event.companion.FORK_APPLY_EVENT:
            content.append(NSAttributedString(string: NSLocalizedString("Timeline.Event.ForkApply", comment: "")))
            
            appendRepoNameIfNeeded()
            
            // final string example: github applied a patch at github/github
            break
        case Event.companion.ORG_BLOCK_EVENT:
            let action: String
            if event.payload?.action == "blocked" {
                action = "Timeline.Event.Blocked"
            } else { // including "unblocked"
                action = "Timeline.Event.Unblocked"
            }
            
            content.append(NSAttributedString(string: NSLocalizedString(action, comment: "")))
            
            if event.payload?.blockedUser != nil {
                appendColoredString(string: (event.payload?.blockedUser?.login)!)
            }
            
            // final string example: github blocked octocat
            break
        case Event.companion.PROJECT_CARD_EVENT:
            let action: String
            switch event.payload?.action {
            case "created":
                action = "Timeline.Event.Created"
                break
            case "updated":
                action = "Timeline.Event.Updated"
                break
            case "moved":
                action = "Timeline.Event.Moved"
                break
            case "converted":
                action = "Timeline.Event.Converted"
                break
            default: // including "deleted"
                action = "Timeline.Event.Deleted"
                break
            }
            
            content.append(NSAttributedString(string: NSLocalizedString(action, comment: "")))
            content.append(NSAttributedString(string: NSLocalizedString("Timeline.Event.ProjectCard", comment: "")))
            content.append(NSAttributedString(string: event.payload?.projectCard?.note ?? ""))
            
            appendRepoNameIfNeeded()
            
            break
        case Event.companion.PROJECT_COLUMN_EVENT:
            let action: String
            switch event.payload?.action {
            case "created":
                action = "Timeline.Event.Created"
                break
            case "updated":
                action = "Timeline.Event.Updated"
                break
            case "moved":
                action = "Timeline.Event.Moved"
                break
            default: // including "deleted"
                action = "Timeline.Event.Deleted"
                break
            }
            
            content.append(NSAttributedString(string: NSLocalizedString(action, comment: "")))
            content.append(NSAttributedString(string: NSLocalizedString("Timeline.Event.ProjectColumn", comment: "")))
            
            if event.payload?.projectColumn != nil {
                appendColoredString(string: (event.payload?.projectColumn?.name)!)
            }
            
            appendRepoNameIfNeeded()
            
            // final string example: github created a project column Small bugfixes at github/github
            break
        case Event.companion.ORGANIZATION_EVENT:
            let action: String
            let toOrFrom: String
            switch event.payload?.action {
            case "member_added":
                action = "Timeline.Event.Added"
                toOrFrom = "Timeline.Event.To"
                break
            case "member_removed":
                action = "Timeline.Event.Removed"
                toOrFrom = "Timeline.Event.From"
                break
            default: // including "member_invited"
                action = "Timeline.Event.Invited"
                toOrFrom = "Timeline.Event.To"
                break
            }
            
            content.append(NSAttributedString(string: NSLocalizedString(action, comment: "")))
            
            if event.payload?.membership?.user != nil {
                appendColoredString(string: event.payload?.membership?.user.login ?? "ghost")
            }
            
            content.append(NSAttributedString(string: NSLocalizedString(toOrFrom, comment: "")))
            
            if event.payload?.organization != nil {
                appendColoredString(string: (event.payload?.organization?.login ?? "ghost"))
            }
            
            // final string example: octocat invited tonnyl to github
            break
        case Event.companion.PROJECT_EVENT:
            let action: String
            switch event.payload?.action {
            case "created":
                action = "Timeline.Event.Created"
                break
            case "edited":
                action = "Timeline.Event.Edited"
                break
            case "closed":
                action = "Timeline.Event.Closed"
                break
            case "reopened":
                action = "Timeline.Event.Reopened"
                break
            default: // including "deleted"
                action = "Timeline.Event.Deleted"
                break
            }
            
            content.append(NSAttributedString(string: NSLocalizedString(action, comment: "")))
            
            if event.payload?.project != nil {
                appendColoredString(string: (event.payload?.project?.name)!)
            }
            
            appendRepoNameIfNeeded()
            
            // final string example: github created a project Space 2.0 at github/github
            break
        default:
            break
        }
        
        return content
    }
    
}

struct EventItem_Previews: PreviewProvider {
    
    static var previews: some View {
        EventItem(
            event: Event(
                id: "14920513551",
                type: "WatchEvent",
                isPublic: true,
                actor: EventActor(
                    id: 8949716,
                    login: "bingoogolapple",
                    avatarUrl: "https://avatars.githubusercontent.com/u/8949716?",
                    htmlUrl: "https://api.github.com/users/bingoogolapple",
                    type: "User"
                ),
                repo: EventRepository(
                    id: "168500397",
                    name: "justauth/JustAuth",
                    fullName: nil,
                    url: "https://api.github.com/repos/justauth/JustAuth",
                    htmlUrl: nil
                ),
                org: EventOrg(
                    id: 53901302,
                    login: "justauth",
                    grAvatarId: "",
                    url: "https://api.github.com/orgs/justauth",
                    avatarUrl: "https://avatars.githubusercontent.com/u/53901302?"
                ),
                createdAt: Kotlinx_datetimeInstant.companion.parse(isoString: "2021-01-23T05:11:13Z"),
                payload: EventPayload(
                    action: "started",
                    comment: nil,
                    commitComment: nil,
                    issue: nil,
                    pullRequest: nil,
                    review: nil,
                    download: nil,
                    target: nil,
                    forkee: nil,
                    gist: nil,
                    pages: nil,
                    member: nil,
                    team: nil,
                    organization: nil,
                    eventRelease: nil,
                    blockedUser: nil,
                    projectCard: nil,
                    projectColumn: nil,
                    membership: nil,
                    invitation: nil,
                    project: nil,
                    size: nil,
                    refType: nil,
                    ref: nil
                ),
                ref: nil,
                refType: nil,
                masterBranch: nil,
                description: nil,
                pusherType: nil,
                head : nil,
                before : nil
            ),
            index: 0,
            totalCount: 1,
            loadMore: {}
        )
    }
    
}

//
//  InboxScreen.swift
//  ios
//
//  Created by lizhaotailang on 2022/2/27.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import SwiftUIX
import common

struct InboxScreen: View {
    
    @EnvironmentObject var viewModel: MainScreenViewModel
    
    var body: some View {
        NavigationView {
            let data = viewModel.notificationsResource?.data?.array ?? []
            let status = viewModel.notificationsResource?.status
            
            ZStack {
                if !data.isEmpty {
                    ScrollView {
                        LazyVStack(alignment: .leading) {
                            ForEach(0..<data.count, id: \.self) { i in
                                NotificationItem(
                                    notification: data[i],
                                    index: i,
                                    totalDataCount: data.count
                                ) {
                                    viewModel.loadNotificationsData(forceRefresh: false)
                                }
                            }
                        }
                    }
                } else if status == .error {
                    EmptyScreen() {
                        viewModel.loadNotificationsData(forceRefresh: true)
                    }
                } else if status == .loading {
                    ActivityIndicator()
                        .animated(true)
                        .style(.regular)
                } else {
                    EmptyScreen(msgString: NSLocalizedString("Common.NoDataFound", comment: ""), actionString: "Common.Retry") {
                        viewModel.loadNotificationsData(forceRefresh: true)
                    }
                }
            }
            .navigationTitle(NSLocalizedString("MainTab.Inbox", comment: ""))
            .navigationBarItems(trailing: ProfileNavigationItem())
        }
    }
    
}

struct NotificationItem: View {
    
    let notification: common.Notification
    let index: Int
    let totalDataCount: Int
    let loadMore: () -> Void
    let login: String
    let repoName: String
    
    init(
        notification: common.Notification,
        index: Int,
        totalDataCount: Int,
        loadMore: @escaping () -> Void
    ) {
        self.notification = notification
        self.totalDataCount = totalDataCount
        self.index = index
        self.loadMore = loadMore
        
        let fullName = notification.repository.fullName.split(separator: "/")
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
                AvatarView(url: self.notification.repository.owner.url)
                VStack(alignment: .leading, spacing: 5) {
                    Text(self.notification.repository.fullName)
                        .font(.headline)
                        .foregroundColor(.systemBlue)
                        .multilineTextAlignment(.leading)
                    Text(self.makeNotificationContent())
                        .multilineTextAlignment(.leading)
                    Text(String(notification.updatedAt.formattedString))
                        .font(.caption)
                        .multilineTextAlignment(.leading)
                }
                Spacer()
            }
            .padding(10)
        }
        .buttonStyle(PlainButtonStyle())
        .onAppear {
            if index == totalDataCount - 1 {
                loadMore()
            }
        }
    }
    
    func makeNotificationContent() -> NSMutableAttributedString {
        let content = NSMutableAttributedString.init()
        
        let action: String
        switch self.notification.reason {
        case common.NotificationReasons.assign:
            action = "Inbox.Notification.Reason.Assign"
            break
        case common.NotificationReasons.author:
            action = "Inbox.Notification.Reason.Author"
            break
        case common.NotificationReasons.comment:
            action = "Inbox.Notification.Reason.Comment"
            break
        case common.NotificationReasons.invitation:
            action = "Inbox.Notification.Reason.Invitation"
            break
        case common.NotificationReasons.manual:
            action = "Inbox.Notification.Reason.Manual"
            break
        case common.NotificationReasons.mention:
            action = "Inbox.Notification.Reason.Mention"
            break
        case common.NotificationReasons.reviewRequested:
            action = "Inbox.Notification.Reason.ReviewRequested"
            break
        case common.NotificationReasons.stateChange:
            action = "Inbox.Notification.Reason.StateChange"
            break
        case common.NotificationReasons.subscribed:
            action = "Inbox.Notification.Reason.Subscribed"
            break
        case common.NotificationReasons.teamMention:
            action = "Inbox.Notification.Reason.TeamMention"
            break
        default:
            action = "Inbox.Notification.Reason.Other"
            break
        }
        
        content.append(NSAttributedString(string: NSLocalizedString(action, comment: "")))
        
        content.addAttribute(NSAttributedString.Key.foregroundColor, value: UIColor.systemBlue, range: NSRange(location: 0, length: content.length))
        
        content.append(NSAttributedString(string: NSLocalizedString("Inbox.Notification.Divider", comment: "")))
        
        content.append(NSAttributedString(string: notification.subject.title))
        
        return content
    }
    
}

struct NotificationItem_Previews: PreviewProvider {
    
    static var previews: some View {
        NotificationItem(
            notification: common.Notification(
                id: "1377378118",
                repository: NotificationRepository(
                    id: 3432266,
                    nodeId: "MDEwOlJlcG9zaXRvcnkzNDMyMjY2",
                    name: "kotlin",
                    fullName: "JetBrains/kotlin",
                    owner: NotificationRepositoryOwner(
                        login: "JetBrains",
                        id: 878437,
                        nodeId: "MDEyOk9yZ2FuaXphdGlvbjg3ODQzNw==",
                        avatarUrl: "https://avatars.githubusercontent.com/u/878437?v=4",
                        gravatarId: "",
                        url: "https://api.github.com/users/JetBrains",
                        htmlUrl: "https://github.com/JetBrains",
                        followersUrl: "https://api.github.com/users/JetBrains/followers",
                        followingUrl: "https://api.github.com/users/JetBrains/following{/other_user}",
                        gistsUrl: "https://api.github.com/users/JetBrains/gists{/gist_id}",
                        starredUrl: "https://api.github.com/users/JetBrains/starred{/owner}{/repo}",
                        subscriptionsUrl: "https://api.github.com/users/JetBrains/subscriptions",
                        organizationsUrl: "https://api.github.com/users/JetBrains/orgs",
                        reposUrl: "https://api.github.com/users/JetBrains/repos",
                        eventsUrl: "https://api.github.com/users/JetBrains/events{/privacy}",
                        receivedEventsUrl: "https://api.github.com/users/JetBrains/received_events",
                        type: "Organization",
                        siteAdmin: false
                    ), isPrivate: false,
                    htmlUrl: "https://github.com/JetBrains/kotlin",
                    description: "The Kotlin Programming Language",
                    fork: false,
                    url: "https://api.github.com/repos/JetBrains/kotlin"
                ),
                subject: NotificationRepositorySubject(
                    title: "Kotlin 1.4.21",
                    url: "https://api.github.com/repos/JetBrains/kotlin/releases/34918478",
                    latestCommentUrl: "https://api.github.com/repos/JetBrains/kotlin/releases/34918478",
                    type: "Release"
                ),
                reason: .subscribed,
                unread: false,
                updatedAt: Kotlinx_datetimeInstant.companion.parse(isoString: "2020-12-09T15:21:46Z"),
                lastReadAt: Kotlinx_datetimeInstant.companion.parse(isoString: "2020-12-08T03:10:28Z"),
                url: "https://api.github.com/notifications/threads/1377378118"
            ),
            index: 0,
            totalDataCount: 2,
            loadMore: {}
        )
    }
    
}

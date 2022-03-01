//
//  ExploreScreen.swift
//  ios
//
//  Created by lizhaotailang on 2021/10/31.
//  Copyright © 2021 orgName. All rights reserved.
//

import common
import SwiftUI
import SwiftUIX

struct ExploreScreen: View {
    @ObservedObject var viewModel = ExploreViewModel()
    
    var body: some View {
        NavigationView {
            let data = viewModel.dataResource?.data
            let status = viewModel.dataResource?.status
            let repos = data?.second.array ?? []
            let developers = data?.first.array ?? []
            
            ZStack {
                if !repos.isEmpty
                    || !developers.isEmpty {
                    ScrollView {
                        LazyVStack(alignment: .leading) {
                            let extraItemCount = developers.isEmpty ? 0 : 1
                            ForEach(0..<repos.count + extraItemCount) { i in
                                if extraItemCount == 1
                                    && i == 0 {
                                    ScrollView(.horizontal, showsIndicators: false) {
                                        LazyHStack {
                                            ForEach(0..<developers.count) { i in
                                                TrendingDeveloperItem(
                                                    developer: developers[i],
                                                    index: i,
                                                    count: developers.count
                                                )
                                            }
                                        }.frame(
                                            minWidth: 0,
                                            maxWidth: .infinity,
                                            minHeight: 80,
                                            maxHeight: .infinity,
                                            alignment: .topLeading
                                        )
                                    }
                                    .padding(.leading, -10)
                                    .padding(.trailing, -10)
                                }
                                
                                if i < repos.count {
                                    TrendingRepositoryItem(repo: repos[i])
                                }
                            }
                        }
                    }
                } else if status == .error {
                    EmptyScreen() {
                        viewModel.refresh()
                    }
                } else if status == .loading {
                    ActivityIndicator()
                        .animated(true)
                        .style(.regular)
                } else {
                    EmptyScreen(msgString: NSLocalizedString("Common.NoDataFound", comment: ""), actionString: "Common.Retry") {
                        viewModel.refresh()
                    }
                }
            }
            .navigationTitle(NSLocalizedString("MainTab.Explore", comment: ""))
        }
    }
}

struct TrendingDeveloperItem: View {
    var developer: TrendingDeveloper
    var index: Int
    var count: Int
    
    var body: some View {
        HStack {
            Spacer(minLength: index == 0 ? 20 : nil)
            NavigationLink(destination: ProfileScreen(login: developer.username, profileType: .user)) {
                VStack(alignment: .center, spacing: 5) {
                    AvatarView(
                        url: developer.avatar,
                        size: 80
                    )
                    Text(developer.name ?? developer.username)
                        .font(.body)
                    Text(developer.username)
                        .font(.caption)
                    Text("\(developer.repository?.name ?? "") - \(developer.repository?.description_ ?? "")")
                        .font(.caption2)
                        .lineLimit(3)
                }
                .frame(width: 180, height: 240, alignment: .center)
                .padding(.leading, 10)
                .padding(.trailing, 10)
                .overlay(
                    RoundedRectangle(cornerRadius: 4.0)
                        .stroke(lineWidth: 1.0)
                        .foregroundColor(.systemGray)
                )
            }.buttonStyle(PlainButtonStyle())
            Spacer(minLength: index == count - 1 ? 20 : 0)
        }
    }
}

struct TrendingRepositoryItem: View {
    var repo: TrendingRepository
    
    var body: some View {
        NavigationLink(destination: RepositoryScreen(login: repo.author, repoName: repo.name)) {
            HStack(alignment: .top, spacing: 10) {
                AvatarView(url: repo.avatar)
                VStack(alignment: .leading, spacing: 5) {
                    Text("\(repo.author)/\(repo.name)")
                        .font(.body)
                        .foregroundColor(.systemBlue)
                    Text(repo.description_ ?? NSLocalizedString("NoDescriptionProvided", comment: ""))
                        .font(.caption)
                        .lineLimit(3)
                    Text(String(format: NSLocalizedString("Explore.PeriodStars", comment: ""), repo.currentPeriodStars, "today"))
                        .font(.caption)
                    HStack(alignment: .center, spacing: 5) {
                        Text(repo.language ?? NSLocalizedString("UnknownLanguage", comment: ""))
                            .font(.caption2)
                        Text("\(repo.stars)")
                            .font(.caption2)
                        Text("\(repo.forks)")
                            .font(.caption2)
                    }
                }
            }
            .padding(10)
        }.buttonStyle(PlainButtonStyle())
    }
}

struct TrendingDeveloperItem_Previews: PreviewProvider {
    
    static var previews: some View {
        TrendingDeveloperItem(
            developer: TrendingDeveloper(
                username: "google",
                name: "Google",
                type: "organization",
                url:"https://github.com/google",
                avatar: "https://avatars0.githubusercontent.com/u/1342004",
                repository: TrendingDeveloperRepository(
                    name: "traceur-compiler",
                    description: "Traceur is a JavaScript.next-to-JavaScript-of-today compiler",
                    url: "https://github.com/google/traceur-compiler"
                )
            ),
            index: 1,
            count: 3
        )
    }
    
}

struct TrendingRepositoryItem_Previews: PreviewProvider {
    
    static var previews: some View {
        TrendingRepositoryItem(
            repo: TrendingRepository(
                author: "google",
                name: "gvisor",
                avatar: "https://github.com/google.png",
                url: "https://github.com/google/gvisor",
                description: "Container Runtime Sandbox",
                language: "Go",
                languageColor: "#3572A5",
                stars: 3320,
                forks: 118,
                currentPeriodStars: 1624,
                builtBy: [
                    TrendingRepositoryBuiltBy(
                        href: "https://github.com/viatsko",
                        avatar: "https://avatars0.githubusercontent.com/u/376065",
                        username: "viatsko"
                    )
                ]
            )
        )
    }
    
}

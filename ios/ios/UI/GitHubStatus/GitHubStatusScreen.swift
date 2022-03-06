//
//  GitHubStatusScreen.swift
//  ios
//
//  Created by lizhaotailang on 2022/3/5.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import SwiftUIX
import common

let StatusColorGreen = Color(hexadecimal: "#28a745")!
let StatusColorOrange = Color(hexadecimal: "#e36209")!
let StatusColorYellow = Color(hexadecimal: "#dbab09")!
let StatusColorRed = Color(hexadecimal: "#dc3545")!
let StatusColorBlue = Color(hexadecimal: "#0366d6")!

extension GitHubStatusComponentStatus {
    
    var color: Color {
        switch self {
        case .operational:
            return StatusColorGreen
        case .degradedperformance:
            return StatusColorYellow
        case .partialoutage:
            return StatusColorOrange
        case .majoroutage:
            return StatusColorRed
        case .undermaintenance:
            return StatusColorBlue
        default:
            return StatusColorBlue
        }
    }
    
    var icon: String {
        switch self {
        case .operational:
            return "checkmark.circle"
        case .degradedperformance:
            return "exclamationmark.triangle"
        case .partialoutage:
            return "exclamationmark.triangle"
        case .majoroutage:
            return "exclamationmark.circle"
        case .undermaintenance:
            return "wrench.and.screwdriver"
        default:
            return "wrench.and.screwdriver"
        }
    }
    
    var displayText: String {
        switch self {
        case .operational:
            return NSLocalizedString("GitHubStatus.Operational", comment: "")
        case .degradedperformance:
            return NSLocalizedString("GitHubStatus.DegradedPerformance", comment: "")
        case .partialoutage:
            return NSLocalizedString("GitHubStatus.PartialOutage", comment: "")
        case .majoroutage:
            return NSLocalizedString("GitHubStatus.MajorOutage", comment: "")
        case .undermaintenance:
            return NSLocalizedString("GitHubStatus.UnderMaintenance", comment: "")
        default:
            return NSLocalizedString("GitHubStatus.UnderMaintenance", comment: "")
        }
    }
    
}

extension GitHubStatusStatusIndicator {
    
    var color: Color {
        switch self {
        case .none:
            return StatusColorGreen
        case .minor:
            return StatusColorOrange
        case .major:
            return StatusColorYellow
        case .critical:
            return StatusColorRed
        case .maintenance:
            return StatusColorBlue
        default:
            return StatusColorBlue
        }
    }
    
    var icon: String {
        switch self {
        case .none:
            return "checkmark"
        case .minor:
            return "exclamationmark"
        case .major:
            return "exclamationmark"
        case .critical:
            return "xmark"
        case .maintenance:
            return "wrench.and.screwdriver"
        default:
            return "wrench.and.screwdriver"
        }
    }
    
}

struct GitHubStatusScreen: View {
    
    @ObservedObject var viewModel = GitHubStatusViewModel()
    
    var body: some View {
        let resource = viewModel.statusResource
        let data = resource?.data
        let components = data?.components ?? []
        let incidents = data?.incidents ?? []
        
        ZStack {
            if data != nil {
                ScrollView {
                    LazyVStack(alignment: .leading) {
                        ForEach(0..<components.count, id: \.self) { i in
                            if i == 0 {
                                GitHubStatusStatusItem(status: data!.status)
                            }
                            GitHubStatusComponentItem(
                                component: components[i],
                                incidents: incidents
                            )
                            if i == components.count - 1 {
                                GitHubStatusPageItem(page: data!.page)
                            }
                        }
                    }
                }
            } else if resource?.status == .success {
                EmptyScreen(msgString: NSLocalizedString("Common.NoDataFound", comment: ""), actionString: "Common.Retry") {
                    viewModel.loadData(forceRefresh: true)
                }
            } else if resource?.status == .error {
                EmptyScreen() {
                    viewModel.loadData(forceRefresh: true)
                }
            } else {
                ActivityIndicator()
                    .animated(true)
                    .style(.regular)
            }
        }.navigationTitle(NSLocalizedString("GitHubStatus", comment: ""))
    }
    
}

struct GitHubStatusComponentItem: View {
    
    let component: GitHubStatusComponent
    let incidents: Array<GitHubIncident>

    let associatedIncident: GitHubIncident?
    
    init(
        component: GitHubStatusComponent,
        incidents: Array<GitHubIncident>
    ) {
        self.component = component
        self.incidents = incidents
        
        associatedIncident = incidents.first(
            where: { incident in
                incident.components.first(
                    where: {
                        $0.id == component.id
                    }
                ) != nil
            }
        )
    }
    
    var body: some View {
        if associatedIncident != nil {
            NavigationLink(destination: GitHubIncidentScreen(incident: associatedIncident!)) {
                content
            }
            .buttonStyle(PlainButtonStyle())
        } else {
            content
        }
    }
    
    private var content: some View {
        VStack {
            HStack {
                VStack(
                    alignment: .leading,
                    spacing: 5
                ) {
                    Text(self.component.name)
                    Text(self.component.status.displayText)
                        .font(.caption)
                }
                .padding(20)
                Spacer()
                Image(systemName: component.status.icon)
                    .foregroundColor(component.status.color)
                    .font(.system(size: 24))
            }
            .padding(.trailing, 20)
            Divider()
                .padding(.leading, 20)
        }
    }
    
}

struct GitHubStatusPageItem: View {
    
    let page: GitHubStatusPage
    
    var body: some View {
        HStack {
            Spacer()
            Text(String(format: NSLocalizedString("GitHubStatus.UpdatedAt", comment: ""), self.page.updatedAt.dateTimeString(timeStyle: .full, dateStyle: .full)))
                .padding(20)
                .font(.caption)
        }
    }
    
}

struct GitHubStatusStatusItem: View {
    
    let status: GitHubStatusStatus
    
    var body: some View {
        HStack {
            Image(systemName: self.status.indicator.icon)
                .foregroundColor(.white)
                .font(.system(size: 24))
            Text(self.status.description_)
                .foregroundColor(.white)
            Spacer()
        }
        .padding(20)
        .background(status.indicator.color)
    }
    
}

struct GitHubStatusComponentItem_Previews: PreviewProvider {
    
    static var previews: some View {
        GitHubStatusComponentItem(
            component: GitHubStatusComponent(
                id: "brv1bkgrwx7q",
                name: "API Requests",
                status:  .operational,
                createdAt: Kotlinx_datetimeInstant.companion.parse(isoString: "2017-01-31T20:01:46.621Z"),
                updatedAt: Kotlinx_datetimeInstant.companion.parse(isoString: "2021-11-27T23:31:00.090Z"),
                position: 2,
                description: "Requests for GitHub APIs",
                showcase: false,
                onlyShowIfDegraded: false
            ),
            incidents: []
        )
    }
    
}

struct GitHubStatusUpdateTimeItem_Previews: PreviewProvider {
    
    static var previews: some View {
        GitHubStatusPageItem(
            page: GitHubStatusPage(
                id: "kctbh9vrtdwd",
                name: "GitHub",
                url: "https://www.githubstatus.com",
                timeZone: "Etc/UTC",
                updatedAt: Kotlinx_datetimeInstant.companion.parse(isoString: "2022-03-01T08:09:21.192Z")
            )
        )
    }
    
}

struct GitHubStatusStatusItem_Previews: PreviewProvider {
    
    static var previews: some View {
        GitHubStatusStatusItem(
            status: GitHubStatusStatus(
                indicator: .none,
                description: "All Systems Operational"
            )
        )
    }
    
}

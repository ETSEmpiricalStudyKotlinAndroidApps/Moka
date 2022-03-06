//
//  GitHubIncident.swift
//  ios
//
//  Created by lizhaotailang on 2022/3/6.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import common

extension GitHubStatusStatusIndicator {
    
    var indicatorColor: Color {
        switch self {
        case .none:
            return StatusColorGreen
        case .minor:
            return StatusColorYellow
        case .major:
            return StatusColorOrange
        case .critical:
            return StatusColorRed
        case .maintenance:
            return StatusColorBlue
        default:
            return StatusColorBlue
        }
    }
    
}

extension GitHubIncidentStatus {
    
    var icon: String {
        switch self {
        case .investigating:
            return "magnifyingglass"
        case .identified:
            return "location"
        case .monitoring:
            return "display"
        case .resolved:
            return  "checkmark"
        case .postmortem:
            return "checklist"
        default:
            return "checklist"
        }
    }
    
}

struct GitHubIncidentScreen: View {
    let incident: GitHubIncident
    
    var body: some View {
        if incident != nil {
            ScrollView {
                LazyVStack(alignment: .leading) {
                    IncidentInfoItem(incident: incident)
                    ForEach(0..<incident.incidentUpdates.count, id: \.self) { i in
                        IncidentUpdateItem(update: incident.incidentUpdates[i])
                    }
                }
            }
            .navigationTitle(NSLocalizedString("GitHubIncident", comment: ""))
        }else {
            EmptyView()
        }
    }
}

struct IncidentUpdateItem: View {
    
    let update: GitHubIncidentUpdate
    
    var body: some View {
        HStack(
            alignment: .top,
            spacing: 20
        ) {
            Image(systemName: update.status.icon)
                .font(.system(size: 24))
                .padding(.top, 5)
            VStack(
                alignment: .leading,
                spacing: 5
            ) {
                Text(String(format: NSLocalizedString("GitHubIncident.StatusAndBody", comment: ""), update.status.name, update.body))
                Text(update.updatedAt.dateTimeString())
                    .font(.caption)
            }
        }
        .padding(20)
    }
    
}

struct IncidentInfoItem: View {
    
    let incident: GitHubIncident
    
    var body: some View {
        Text(incident.name)
            .foregroundColor(incident.impact.indicatorColor)
            .padding(20)
    }
    
}

struct IncidentInfoItem_Previews: PreviewProvider {
    static var previews: some View {
        IncidentInfoItem(
            incident: GitHubIncident(
                id: "416ft5p3mr89",
                name: "Incident with Pull Requests",
                status: .investigating,
                createdAt: Kotlinx_datetimeInstant.companion.parse(isoString: "2022-03-04T15:30:22.821Z"),
                updatedAt: Kotlinx_datetimeInstant.companion.parse(isoString: "2022-03-04T15:30:31.536Z"),
                monitoringAt: nil,
                resolvedAt: nil,
                impact: .minor,
                shortlink: "https://stspg.io/yfjxdqbc12vh",
                startedAt: Kotlinx_datetimeInstant.companion.parse(isoString: "2022-03-04T15:30:22.815Z"),
                pageId: "kctbh9vrtdwd",
                incidentUpdates: [],
                components: []
            )
        )
    }
}

struct IncidentUpdateItem_Previews: PreviewProvider {
    
    static var previews: some View {
        IncidentUpdateItem(
            update: GitHubIncidentUpdate(
                id: "lcc22kp4qg68",
                status: .resolved,
                body: "This incident has been resolved.",
                incidentId: "416ft5p3mr89",
                createdAt: Kotlinx_datetimeInstant.companion.parse(isoString: "2022-03-04T16:37:48.602Z"),
                updatedAt: Kotlinx_datetimeInstant.companion.parse(isoString: "2022-03-04T16:37:48.602Z"),
                displayAt: Kotlinx_datetimeInstant.companion.parse(isoString: "2022-03-04T16:37:48.602Z"),
                affectedComponents: nil,
                deliverNotifications: true,
                customTweet: nil,
                tweetId: nil
            )
        )
    }
    
}

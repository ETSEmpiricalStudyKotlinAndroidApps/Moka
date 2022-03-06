//
//  Data+Extensions.swift
//  ios
//
//  Created by lizhaotailang on 2022/2/27.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import common
import Then
import SwiftUI

extension TrendingDeveloper: Identifiable {
    
}
extension TrendingRepository: Identifiable {
    
}

extension Event: Identifiable {
    
}
extension common.Notification: Identifiable {
    
}
extension CommitFile: Identifiable {
    
}
extension IssueListItem: Identifiable {
    
}
extension PullRequestListItem: Identifiable {
    
}
extension GitHubStatus: Identifiable {
    
}

private func formatDate(
    date: Foundation.Date,
    timeStyle: DateFormatter.Style = .none,
    dateStyle: DateFormatter.Style = .full
) -> String {
    let dateFormat = DateFormatter.dateFormat(fromTemplate: "yyyyMMdd'T'HH:mm:ss", options: 0, locale: Locale.current)
    
    return DateFormatter().then {
        $0.timeStyle = timeStyle
        $0.dateStyle = dateStyle
        $0.dateFormat = dateFormat
    }.string(from: date)
}

extension Kotlinx_datetimeInstant {
    public var formattedString: String {
        let now = Foundation.Date()
        let diff = Int64(now.timeIntervalSince1970) - epochSeconds
        
        if diff <= DateTimeExtensionsKt.OneMinuteInSeconds {
            return NSLocalizedString("Common.Time.JustNow", comment: "just now")
        }
        
        if diff < DateTimeExtensionsKt.OneHourInSeconds { // 60 minutes
            return String(format: NSLocalizedString("Common.Time.MinutesAgo", comment: ""), diff / DateTimeExtensionsKt.OneMinuteInSeconds)
        }
        
        if diff < DateTimeExtensionsKt.OneHourInSeconds * 2 {
            return NSLocalizedString("Common.Time.OneHourAgo", comment: "")
        }
        
        if diff < DateTimeExtensionsKt.OneDayInSeconds {
            return String(format: NSLocalizedString("Common.Time.HoursAgo", comment: ""), diff / DateTimeExtensionsKt.OneHourInSeconds)
        }
        
        if diff < DateTimeExtensionsKt.OneDayInSeconds * 2 {
            return NSLocalizedString("Common.Time.OneDayAgo", comment: "")
        }
        
        if diff < DateTimeExtensionsKt.OneDayInSeconds * 10 {
            return String(format: NSLocalizedString("Common.Time.DaysAgo", comment: ""), diff / DateTimeExtensionsKt.OneDayInSeconds)
        }
        
        return formatDate(date: now.addingTimeInterval(TimeInterval(diff)))
    }
    
    public func dateTimeString(
        timeStyle: DateFormatter.Style = .none,
        dateStyle: DateFormatter.Style = .full
    ) -> String {
        let now = Foundation.Date()
        let diff = Int64(now.timeIntervalSince1970) - epochSeconds
        
        return formatDate(
            date: now.addingTimeInterval(TimeInterval(diff)),
            timeStyle: timeStyle,
            dateStyle: dateStyle
        )
    }
    
}

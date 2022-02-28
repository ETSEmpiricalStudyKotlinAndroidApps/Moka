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

private func formatDate(date: Foundation.Date) -> String {
    let dateFormat = DateFormatter.dateFormat(fromTemplate: "yyyyMMdd", options: 0, locale: Locale.current)
    
    return DateFormatter().then {
        $0.timeStyle = .none
        $0.dateStyle = .full
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
}

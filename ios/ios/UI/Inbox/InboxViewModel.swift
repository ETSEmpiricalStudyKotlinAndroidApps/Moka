//
//  InboxViewModel.swift
//  ios
//
//  Created by lizhaotailang on 2022/2/27.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import common

class InboxViewModel: ObservableObject {
    
    @Published var notificationsResource: Resource<ArrayClass<common.Notification>>? = nil
    
    private let api = NotificationApiWrapper(api: NotificationApi(ktorClient: KtorClient(requireAuth: false, accessToken: nil).httpClient))
    private var pl: PageLinks? = nil
    
    init() {
        loadData(forceRefresh: true)
    }
    
    func loadData(forceRefresh: Bool) {
        if forceRefresh {
            pl = nil
        } else {
            if notificationsResource != nil
                && notificationsResource?.status == .loading {
                return
            }
        }
        
        self.notificationsResource = Resource(status: .loading, data: self.notificationsResource?.data, e: nil)
        if (pl?.next).isNullOrEmpty {
            api.listNotifications(all: true, page: 0, perPage: 16) { resp, error in
                self.handleResp(
                    forceRefresh: forceRefresh,
                    resp: resp,
                    error: error == nil ? nil : KotlinException(message: error?.localizedDescription)
                )
            }
        } else {
            api.listNotificationsByUrl(url: (pl?.next)!) { resp, error in
                self.handleResp(
                    forceRefresh: forceRefresh,
                    resp: resp,
                    error: error == nil ? nil : KotlinException(message: error?.localizedDescription)
                )
            }
        }
    }
    
    private func handleResp(
        forceRefresh: Bool,
        resp: common.Result?,
        error: KotlinException?
    ) {
        if error == nil {
            if resp is common.ResultSuccess<Pair<NSArray, PageLinks>> {
                let success = (resp as! common.ResultSuccess<common.Pair<NSArray, PageLinks>>).value
                self.pl = success?.second
                let notifications: [common.Notification] = success?.first?.compactMap({ $0 as? common.Notification }) ?? []
                var existing: Array<common.Notification>
                if forceRefresh {
                    existing = []
                } else {
                    existing = notificationsResource?.data?.array ?? []
                }
                existing.append(contentsOf: notifications)
                
                self.notificationsResource = Resource(status: .success, data: ArrayClass(array: existing), e: nil)
            } else {
                let failure = resp as! common.ResultFailure<common.Pair<NSArray, PageLinks>>
                self.notificationsResource = Resource(status: .error, data: nil, e: failure.error)
            }
        } else {
            self.notificationsResource = Resource(status: .error, data: nil, e: error)
        }
    }
    
}

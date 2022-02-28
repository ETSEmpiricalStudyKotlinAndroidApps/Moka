//
//  TimelineViewModel.swift
//  ios
//
//  Created by lizhaotailang on 2022/2/27.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import common

class TimelineViewModel: ObservableObject {
    
    @Published var eventsResource: Resource<ArrayClass<Event>>? = nil
    
    private let api = EventApiWrapper(api: EventApi(ktorClient: KtorClient(requireAuth: false, accessToken: nil).httpClient))
    private var pl: PageLinks? = nil
    
    init() {
        loadData(forceRefresh: true)
    }
    
    func loadData(forceRefresh: Bool) {
        if forceRefresh {
            pl = nil
        } else {
            if eventsResource != nil
                && eventsResource?.status == .loading {
                return
            }
        }
        
        self.eventsResource = Resource(status: .loading, data: self.eventsResource?.data, e: nil)
        
        if (pl?.next).isNullOrEmpty {
            api.listPublicEventThatAUserHasReceived(username: "TonnyL", page: 0, perPage: 16) { [self] resp, error in
                self.handleResp(
                    forceRefresh: forceRefresh,
                    resp: resp,
                    error: error == nil ? nil : KotlinException(message: error?.localizedDescription)
                )
            }
        } else {
            api.listPublicEventThatAUserHasReceivedByUrl(url: (pl?.next)!) { resp, error in
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
            if resp is common.ResultSuccess<common.Pair<NSArray, PageLinks>> {
                let success = (resp as! common.ResultSuccess<common.Pair<NSArray, PageLinks>>).value
                self.pl = success?.second
                let events: [Event] = success?.first?.compactMap({ $0 as? Event }) ?? []
                var existing: Array<Event>
                if forceRefresh {
                    existing = []
                } else {
                    existing = eventsResource?.data?.array ?? []
                }
                existing.append(contentsOf: events)
                
                self.eventsResource = Resource(status: .success, data: ArrayClass(array: existing), e: nil)
            } else {
                let failure = resp as! common.ResultFailure<common.Pair<NSArray, PageLinks>>
                self.eventsResource = Resource(status: .error, data: nil, e: failure.error)
            }
        } else {
            self.eventsResource = Resource(status: .error, data: nil, e: error)
        }
    }
    
}

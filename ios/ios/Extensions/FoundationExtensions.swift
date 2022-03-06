//
//  FoundationExtensions.swift
//  ios
//
//  Created by lizhaotailang on 2022/2/28.
//  Copyright © 2022 orgName. All rights reserved.
//

import common

extension Optional where Wrapped  == String {
    
    var isNullOrEmpty: Bool {
        return self?.isEmpty ?? true
    }
    
    var isNullOrBlank: Bool {
        return self?.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ?? true
    }
    
    var orEmpty: String {
        return self ?? ""
    }
    
    func ifNullOrEmpty(defaultValue: () -> String) -> String {
        if self.isNullOrEmpty {
            return defaultValue()
        }
        
        return self!
    }
    
}

extension Error {
    
    var kotlinException: KotlinException {
        return KotlinException(message: self.localizedDescription)
    }
    
}

require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name         = 'RNZoomUs'
  s.version      = package['version']
  s.summary      = package['description']
  s.description  = package['description']
  s.license      = package['license']
  s.author       = package['author']
  s.homepage     = package['homepage']
  s.platform     = :ios, '9.0'
  s.source       = { git: 'https://github.com/mieszko4/react-native-zoom-us', tag: 'master' }
  s.source_files = 'ios/*.{h,m}'
  s.requires_arc = true

  # [TODO] Replace with zoom pod
  # https://github.com/pavelsmelovsky/ZoomSDK
  s.subspec 'Core' do |subspec|
    subspec.ios.source_files           = 'ios/libs/MobileRTC.framework/Headers/**/*.{h,m}'
    subspec.ios.public_header_files    = 'ios/libs/MobileRTC.framework/Headers/**/*.{h,m}'
    subspec.ios.vendored_frameworks    = 'ios/libs/MobileRTC.framework'
    subspec.ios.resource = 'ios/libs/MobileRTCResources.bundle'
  end

  s.dependency 'React'
end


import { NativeModules } from 'react-native'

const { RNMotionViews } = NativeModules

export const startWithOptions = async ({ icons, fontFamily, originalBackgroundImagePath, editedBackgroundImagePath, savePermission }) => {
  // DeviceEventEmitter.addListener(RNMotionViews.events.checkPermissionEvent, async (event) => {
  //   if (savePermissionCallback && typeof savePermissionCallback === 'function') {
  //     const premissionGranted = await savePermissionCallback()
  //     console.log('premissionGranted', premissionGranted)
  //   }
  // })
  return RNMotionViews.startWithOptions({
    generalConfig: {
      originalBackgroundImagePath,
      editedBackgroundImagePath,
      fontFamily: fontFamily,
      initialToolSelection: 'penTool',
      initialText: '',
      backgroundColor: '#192d2e',
      savePermission
    },
    buttonConfigs: [{
      screen: 'allScreens',
      configs: [
        {
          id: 'cancelButtonConfig',
          enabled: true,
          icon: {},
          label: '',
          tint: '#FFFFFF'
        }, {
          id: 'clearButtonConfig',
          enabled: true,
          icon: {},
          label: '',
          tint: '#FFFFFF'
        }, {
          id: 'saveButtonConfig',
          enabled: true,
          icon: {},
          label: null,
          tint: '#FFFFFF'
        }, {
          id: 'penToolConfig',
          enabled: true,
          icon: {},
          label: null,
          tint: '#FFFFFF'
        }, {
          id: 'eraseToolConfig',
          enabled: true,
          icon: {},
          label: null,
          tint: '#FFFFFF'
        }, {
          id: 'circleToolConfig',
          enabled: true,
          icon: {},
          label: null,
          tint: '#FFFFFF'
        }, {
          id: 'arrowToolConfig',
          enabled: true,
          icon: {},
          label: '',
          tint: '#FFFFFF'
        }, {
          id: 'createTextConfig',
          enabled: true,
          icon: {},
          label: '',
          tint: '#FFFFFF',
          sideLength: 44 * 3,
          paddingTop: 12 * 3
        }, {
          id: 'createSketchConfig',
          enabled: true,
          icon: {},
          label: '',
          tint: '#FFFFFF',
          sideLength: 44 * 3,
          paddingTop: 12 * 3
        }, {
          id: 'createStickerConfig',
          enabled: false,
          icon: {},
          label: '',
          tint: '#FFFFFF',
          sideLength: 44 * 3,
          paddingTop: 12 * 3
        }
      ]
    }, {
      screen: 'textEntityScreen',
      configs: [
        {
          id: 'cancelButtonConfig',
          enabled: true,
          icon: {},
          label: '',
          tint: '#FFFFFF'
        }, {
          id: 'clearButtonConfig',
          enabled: true,
          icon: {},
          label: null,
          tint: '#FFFFFF'
        }, {
          id: 'saveButtonConfig',
          enabled: true,
          icon: {},
          label: '',
          tint: '#FFFFFF'
        }
      ]
    }, {
      screen: 'mainScreen',
      configs: [
        {
          id: 'cancelButtonConfig',
          enabled: true,
          icon: {},
          label: '',
          tint: '#FFFFFF',
          sideLength: 44 * 3
        }, {
          id: 'saveButtonConfig',
          enabled: true,
          icon: {},
          label: '',
          tint: '#FFFFFF',
          sideLength: 44 * 3
        }, {
          id: 'deleteButtonConfig',
          enabled: true,
          icon: icons.trashIcon ? icons.trashIcon : null,
          label: '',
          tint: '#FFFFFF',
          sideLength: 44 * 3
        }, {
          id: 'clearButtonConfig',
          enabled: true,
          icon: {},
          label: '',
          tint: '#FFFFFF',
          sideLength: 44 * 3
        }, {
          id: 'trashButtonConfig',
          enabled: true,
          icon: icons.trashIcon ? icons.trashIcon : null,
          label: '',
          tint: '#FFFFFF'
        }
      ]
    }],
    colorConfig: [
      {
        screen: 'allScreens',
        enabled: true,
        initialColor: '#FFFFFF',
        colors: ['#000000', '#20BBFC', '#2DFD2F', '#FD28F9', '#EA212E', '#FD7E24', '#FFFA38', '#FFFFFF'],
        pickerConfig: {
          enabled: true,
          icon: {},
          pickerLabel: 'Some label',
          cancelText: 'cancel',
          submitText: 'ok'
        }
      }
    ],
    sizeConfig: [
      {
        screen: 'sketchEntityScreen',
        enabled: true,
        backgroundColor: null,
        progressColor: null,
        initialValue: 5,
        min: 2,
        max: 40,
        step: 1
      },
      {
        screen: 'textEntityScreen',
        enabled: true,
        backgroundColor: null,
        progressColor: null,
        initialValue: 30,
        min: 15,
        max: 70,
        step: 1
      }
    ]
  })
}
// can override allScreens settings with specific setting and vice versa (setting on top is overridden)(buttonsConfig arraylist is even merged, configs themselves are replaced --> TODO: implement MergeConfig interface to add real merge functionality)
// missing key (not explicitly tested) / empty string / null can be used to do not pass an option

// const defaultOptions = {
//   generalConfig: {
//     backgroundImagePath: null, // to be set dynamically
//     fontFamily: 'pero_regular.otf',
//     initialToolSelection: 'penTool',
//     initialText: ''
//   },
//   buttonConfigs: [{
//     screen: 'allScreens',
//     configs: [
//       {
//         id: 'cancelButtonConfig',
//         enabled: true,
//         icon: {},
//         label: '',
//         tint: '#FFFFFF'
//       }, {
//         id: 'clearButtonConfig',
//         enabled: true,
//         icon: {},
//         label: '',
//         tint: '#FFFFFF'
//       }, {
//         id: 'saveButtonConfig',
//         enabled: true,
//         icon: {},
//         label: null,
//         tint: '#FFFFFF'
//       }, {
//         id: 'penToolConfig',
//         enabled: true,
//         icon: {},
//         label: null,
//         tint: '#FFFFFF'
//       }, {
//         id: 'eraseToolConfig',
//         enabled: true,
//         icon: {},
//         label: null,
//         tint: '#FFFFFF'
//       }, {
//         id: 'circleToolConfig',
//         enabled: true,
//         icon: {},
//         label: null,
//         tint: '#FFFFFF'
//       }, {
//         id: 'arrowToolConfig',
//         enabled: true,
//         icon: {},
//         label: '',
//         tint: '#FFFFFF'
//       }, {
//         id: 'createTextConfig',
//         enabled: true,
//         icon: {},
//         label: '',
//         tint: '#FFFFFF',
//         sideLength: 44 * 3,
//         paddingTop: 12 * 3
//       }, {
//         id: 'createSketchConfig',
//         enabled: true,
//         icon: {},
//         label: '',
//         tint: '#FFFFFF',
//         sideLength: 44 * 3,
//         paddingTop: 12 * 3
//       }, {
//         id: 'createStickerConfig',
//         enabled: false,
//         icon: {},
//         label: '',
//         tint: '#FFFFFF',
//         sideLength: 44 * 3,
//         paddingTop: 12 * 3
//       }
//     ]
//   }, {
//     screen: 'textEntityScreen',
//     configs: [
//       {
//         id: 'cancelButtonConfig',
//         enabled: true,
//         icon: {},
//         label: '',
//         tint: '#FFFFFF'
//       }, {
//         id: 'clearButtonConfig',
//         enabled: true,
//         icon: {},
//         label: null,
//         tint: '#FFFFFF'
//       }, {
//         id: 'saveButtonConfig',
//         enabled: true,
//         icon: {},
//         label: '',
//         tint: '#FFFFFF'
//       }
//     ]
//   }, {
//     screen: 'mainScreen',
//     configs: [
//       {
//         id: 'cancelButtonConfig',
//         enabled: true,
//         icon: {},
//         label: '',
//         tint: '#FFFFFF',
//         sideLength: 44 * 3
//       }, {
//         id: 'saveButtonConfig',
//         enabled: true,
//         icon: {},
//         label: '',
//         tint: '#FFFFFF',
//         sideLength: 44 * 3
//       }, {
//         id: 'trashButtonConfig',
//         enabled: true,
//         icon: {
//           name: 'icon32-trash@1x.png'
//         },
//         label: '',
//         tint: '#FFFFFF'
//       }
//     ]
//   }],
//   colorConfig: [
//     {
//       screen: 'allScreens',
//       enabled: true,
//       initialColor: '#FFFFFF',
//       colors: ['#000000', '#20BBFC', '#2DFD2F', '#FD28F9', '#EA212E', '#FD7E24', '#FFFA38', '#FFFFFF'],
//       pickerConfig: {
//         enabled: true,
//         icon: {},
//         pickerLabel: 'Some label',
//         cancelText: 'cancel',
//         submitText: 'ok'
//       }
//     }
//   ],
//   sizeConfig: [
//     {
//       screen: 'sketchEntityScreen',
//       enabled: true,
//       backgroundColor: null,
//       progressColor: null,
//       initialValue: 5,
//       min: 2,
//       max: 40,
//       step: 1
//     },
//     {
//       screen: 'textEntityScreen',
//       enabled: true,
//       backgroundColor: null,
//       progressColor: null,
//       initialValue: 30,
//       min: 15,
//       max: 70,
//       step: 1
//     }
//   ]
// }

/**
*  TODO LIST init params:
*

* - default opened 'create entity action' (e.g. sketchView)
*/

export default RNMotionViews

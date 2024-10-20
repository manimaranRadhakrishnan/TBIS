import { INavbarData } from "./helper";

export const navbarData: INavbarData[] = [
    {
        routeLink: 'report/35',
        icon: 'bi bi-grid',
        label: 'Dasshboardadfdf'
    },
    {
        routeLink: 'masters',
        icon: 'bi bi-menu-button-wide',
        label: 'Masters',
        items: [
            {
                routeLink: 'report/35',
                icon: 'bi bi-menu-button-wide',
                label: 'Customer Master',
                items: [
                    {
                        routeLink: 'report/35',
                        label: 'Level 2.1',
                    },
                    {
                        routeLink: 'products/level2.2',
                        label: 'Level 2.2',
                        items: [
                            {
                                routeLink: 'report/3',
                                label: 'Level 3.1'
                            },
                            {
                                routeLink: 'report/4',
                                label: 'Level 3.2'
                            }
                        ]
                    }
                ]
            },
            {
                routeLink: 'report/5',
                label: 'Level 1.2',
            }
        ]
    },
    {
        routeLink: 'statistics',
        icon: 'bi bi-journal-text',
        label: 'Transactions'
    },
    {
        routeLink: 'Reports',
        icon: 'bi bi-layout-text-window-reverse',
        label: 'Coupens',
        items: [
            {
                routeLink: 'coupens/list',
                label: 'List Coupens'
            },
            {
                routeLink: 'coupens/create',
                label: 'Create Coupens'
            }
        ]
    },
    {
        routeLink: 'pages',
        icon: 'fal fa-file',
        label: 'Pages'
    },
    {
        routeLink: 'media',
        icon: 'fal fa-camera',
        label: 'Media'
    },
    {
        routeLink: 'settings',
        icon: 'fal fa-cog',
        label: 'Settings',
        expanded: true,
        items: [
            {
                routeLink: 'settings/profile',
                label: 'Profile'
            },
            {
                routeLink: 'settings/customize',
                label: 'Customize'
            }
        ]
    },
];
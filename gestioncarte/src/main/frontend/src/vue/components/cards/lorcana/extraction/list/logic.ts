import {ExtractionStatusLegendType} from "@components/extraction";

export const legends: ExtractionStatusLegendType[] = [
    { iconName: "mail-unread-outline", color: "success", text: "Nouvelle carte", key: 'new' },
    { iconName: "git-merge", text: "Mariage automatique", key: 'merge' },
    { iconName: "git-merge", color: "danger", text: "Mariage conflictuel", key: 'breaking-merge' },
    { iconSrc: "/svg/db.svg", color: "warning", text: "Carte avec modification", key: 'change' },
    { iconSrc: "/svg/db.svg", color: "danger", text: "Carte avec modification bloquante", key: 'breaking-change' },
    { iconName: "file-tray-outline", color: "danger", text: "Non traduit dans toutes les langues", key: 'missing-translation' },
    { iconName: "warning-outline", color: "danger", text: "Conflit entre cartes", key: 'duplicate' },
    { iconName: "language-outline", color: "danger", text: "Conflit de traduction", key: 'translation-conflict' },
    { iconName: "language-outline", color: "success", text: "Nouveau charset", key: 'new-charset' },
    { iconName: "close-outline", color: "danger", text: "Carte à vérifier", key: 'invalid' },
    { iconName: "checkmark-outline", color: "success", text: "Carte vérifiée", key: 'reviewed' }
];

import {RevisionDTO} from "@/types/RevisionDTO";

export interface HistoryTreeDTO<T> {
    revision: RevisionDTO<T>,
    parents: HistoryTreeDTO<T>[]
}

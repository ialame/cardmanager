import {LorcanaSerieDTO, Service} from "@/types";

export const lorcanaSerieService = new Service<LorcanaSerieDTO, string>('/api/cards/lorcana/series');

import React, {Component} from 'react';

class ListArtistComponent extends Component {

    constructor(props) {
        super(props);
        this.state = {
            artists: []
        }
    }
    render() {
        return (
            <div>
                <h2 className = "text-center"> Artists list</h2>
                <div className = "row">
                    <table className = "table table-striped table-bordered">
                        <thead>
                            <tr>
                                <th> Name</th>
                                <th> Surname</th>
                                <th> Actions</th>
                            </tr>
                        </thead>

                        <tbody>
                        {
                            this.state.artists.map(artist =>
                            <tr key={artist.id}>
                                <td> {artist.name}</td>
                                <td> {artist.surname}</td>
                            </tr>)
                        }
                        </tbody>
                    </table>
                </div>

            </div>
        );
    }
}

export default ListArtistComponent;